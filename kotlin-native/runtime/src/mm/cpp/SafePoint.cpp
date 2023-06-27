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

std::atomic<int64_t> activeCount = 0;

ALWAYS_INLINE void slowPathImpl(mm::ThreadData& threadData) noexcept {
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

    // reread an action to avoid register pollution outside the function
    auto count = activeCount.load(std::memory_order_acquire);
    RuntimeAssert(count >= 0, "Unexpected activeCount: %" PRId64, count);
    if (count == 0) {
        return;
    }
    threadData.suspensionData().suspendIfRequested();
    mm::GlobalData::Instance().gcScheduler().safePoint();
}

NO_INLINE void slowPath() noexcept {
    slowPathImpl(*mm::ThreadRegistry::Instance().CurrentThreadData());
}

NO_INLINE void slowPath(mm::ThreadData& threadData) noexcept {
    slowPathImpl(threadData);
}

void incrementActiveCount() noexcept {
    auto count = activeCount.fetch_add(1, std::memory_order_release);
    RuntimeAssert(count >= 0, "Unexpected activeCount: %" PRId64, count);
}

void decrementActiveCount() noexcept {
    auto count = activeCount.fetch_sub(1, std::memory_order_release);
    RuntimeAssert(count >= 1, "Unexpected activeCount: %" PRId64, count);
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
    auto count = activeCount.load(std::memory_order_relaxed);
    RuntimeAssert(count >= 0, "Unexpected activeCount: %" PRId64, count);
    if (__builtin_expect(count > 0, false)) {
        slowPath();
    }
}

ALWAYS_INLINE void mm::safePoint(mm::ThreadData& threadData) noexcept {
    AssertThreadState(&threadData, ThreadState::kRunnable);
    auto count = activeCount.load(std::memory_order_relaxed);
    RuntimeAssert(count >= 0, "Unexpected activeCount: %" PRId64, count);
    if (__builtin_expect(count > 0, false)) {
        slowPath(threadData);
    }
}
