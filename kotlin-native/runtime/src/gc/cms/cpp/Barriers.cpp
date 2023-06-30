/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "Barriers.hpp"

#include <algorithm>
#include <atomic>

#include "GCImpl.hpp"
#include "SafePoint.hpp"
#include "ThreadData.hpp"
#include "ThreadRegistry.hpp"

using namespace kotlin;

namespace {

[[clang::no_destroy]] std::atomic<bool> weakRefBarriersEnabled = false;

void waitForThreadsToReachCheckpoint() {
    // Reset checkpoint on all threads.
    for (auto& thr : mm::ThreadRegistry::Instance().LockForIter()) {
        thr.gc().impl().gc().barriers().resetCheckpoint();
    }

    mm::SafePointActivator safePointActivator;

    // Disable new threads coming and going.
    auto threads = mm::ThreadRegistry::Instance().LockForIter();
    // And wait for all threads to either have passed safepoint or to be in the native state.
    // Either of these mean that none of them are inside a weak reference accessing code.
    while (!std::all_of(threads.begin(), threads.end(), [](mm::ThreadData& thread) noexcept {
        return thread.gc().impl().gc().barriers().visitedCheckpoint() || thread.suspensionData().suspendedOrNative();
    })) {
        std::this_thread::yield();
    }
}

} // namespace

void gc::BarriersThreadData::onCheckpoint() noexcept {
    visitedCheckpoint_.store(true, std::memory_order_release);
}

void gc::BarriersThreadData::resetCheckpoint() noexcept {
    visitedCheckpoint_.store(false, std::memory_order_release);
}

bool gc::BarriersThreadData::visitedCheckpoint() const noexcept {
    return visitedCheckpoint_.load(std::memory_order_acquire);
}

void gc::EnableWeakRefBarriers() noexcept {
    weakRefBarriersEnabled.store(true, std::memory_order_seq_cst);
}

void gc::DisableWeakRefBarriers() noexcept {
    weakRefBarriersEnabled.store(false, std::memory_order_seq_cst);
    waitForThreadsToReachCheckpoint();
}

OBJ_GETTER(kotlin::gc::WeakRefRead, ObjHeader* weakReferee) noexcept {
    if (compiler::concurrentWeakSweep()) {
        if (weakReferee != nullptr) {
            // weakRefBarriersEnabled changes are synchronized with checkpoints or STW
            if (weakRefBarriersEnabled.load(std::memory_order_relaxed)) {
                // When weak ref barriers are enabled, marked state cannot change and the
                // object cannot be deleted.
                if (!gc::isMarked(weakReferee)) {
                    RETURN_OBJ(nullptr);
                }
            }
        }
    }
    RETURN_OBJ(weakReferee);
}
