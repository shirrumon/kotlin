/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "SafePoint.hpp"

#include <atomic>

#include "GCScheduler.hpp"
#include "KAssert.h"
#include "ThreadData.hpp"
#include "ThreadState.hpp"

using namespace kotlin;

namespace {

void safePointActionImpl(mm::ThreadData& threadData) noexcept {
    // Changing thread state can lead to a safe point.
    // Thread state change may come from inside safe point procedure.
    // Let's just guard against it.
    static thread_local bool recursion = false;
    if (recursion) {
        return;
    }
    class RecursionGuard : private Pinned {
    public:
        RecursionGuard() noexcept { recursion = true; }
        ~RecursionGuard() { recursion = false; }
    } guard;

    std::atomic_thread_fence(std::memory_order_acquire);
    threadData.suspensionData().suspendIfRequested();
    mm::GlobalData::Instance().gcScheduler().safePoint();
}

void safePointActionImpl() noexcept {
    safePointActionImpl(*mm::ThreadRegistry::Instance().CurrentThreadData());
}

void safePointActionNoop(mm::ThreadData& threadData) noexcept {}
void safePointActionNoop() noexcept {}

[[clang::no_destroy]] std::mutex safePointActionMutex;
int64_t activeCount = 0;
std::atomic<void(*)(mm::ThreadData&)> safePointAction = safePointActionImpl;
std::atomic<void(*)()> safePointActionNoThreadData = safePointActionNoop;

void incrementActiveCount() noexcept {
    std::unique_lock guard{safePointActionMutex};
    ++activeCount;
    RuntimeAssert(activeCount >= 1, "Unexpected activeCount: %" PRId64, activeCount);
    if (activeCount == 1) {
        safePointAction.store(safePointActionImpl, std::memory_order_relaxed);
        safePointActionNoThreadData.store(safePointActionImpl, std::memory_order_relaxed);
        std::atomic_thread_fence(std::memory_order_release);
    }
}

void decrementActiveCount() noexcept {
    std::unique_lock guard{safePointActionMutex};
    --activeCount;
    RuntimeAssert(activeCount >= 0, "Unexpected activeCount: %" PRId64, activeCount);
    if (activeCount == 0) {
        safePointAction.store(safePointActionNoop, std::memory_order_relaxed);
        safePointActionNoThreadData.store(safePointActionNoop, std::memory_order_relaxed);
        std::atomic_thread_fence(std::memory_order_release);
    }
}

} // namespace

mm::SafePointActivator::SafePointActivator() noexcept : active_(true) {
    incrementActiveCount();
}

mm::SafePointActivator::~SafePointActivator() {
    if (active_) {
        decrementActiveCount();
    }
}

ALWAYS_INLINE void mm::safePoint() noexcept {
    AssertThreadState(ThreadState::kRunnable);
    safePointActionNoThreadData.load(std::memory_order_relaxed)();
}

ALWAYS_INLINE void mm::safePoint(mm::ThreadData& threadData) noexcept {
    AssertThreadState(&threadData, ThreadState::kRunnable);
    safePointAction.load(std::memory_order_relaxed)(threadData);
}
