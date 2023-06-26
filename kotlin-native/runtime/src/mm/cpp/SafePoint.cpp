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

std::atomic<mm::SafePointAction> safePointAction = nullptr;

ALWAYS_INLINE void safePointActionImpl(mm::ThreadData& threadData) noexcept {
    // reread an action to avoid register pollution outside the function
    auto action = safePointAction.load(std::memory_order_seq_cst);
    if (action != nullptr) {
        action(threadData);
    }
}

NO_INLINE void slowPath() noexcept {
    safePointActionImpl(*mm::ThreadRegistry::Instance().CurrentThreadData());
}

NO_INLINE void slowPath(mm::ThreadData& threadData) noexcept {
    safePointActionImpl(threadData);
}

} // namespace

bool mm::trySetSafePointAction(mm::SafePointAction action) noexcept {
    mm::SafePointAction expected = nullptr;
    mm::SafePointAction desired = action;
    return safePointAction.compare_exchange_strong(expected, desired, std::memory_order_seq_cst);
}

void mm::unsetSafePointAction() noexcept {
    auto prevAction = safePointAction.exchange(nullptr, std::memory_order_seq_cst);
    RuntimeAssert(prevAction != nullptr, "Some safe point action must have been set");
}

ALWAYS_INLINE void mm::safePoint() noexcept {
    AssertThreadState(ThreadState::kRunnable);
    mm::GlobalData::Instance().gcScheduler().safePoint();
    auto action = safePointAction.load(std::memory_order_relaxed);
    if (__builtin_expect(action != nullptr, false)) {
        slowPath();
    }
}

ALWAYS_INLINE void mm::safePoint(mm::ThreadData& threadData) noexcept {
    AssertThreadState(&threadData, ThreadState::kRunnable);
    mm::GlobalData::Instance().gcScheduler().safePoint();
    auto action = safePointAction.load(std::memory_order_relaxed);
    if (__builtin_expect(action != nullptr, false)) {
        slowPath(threadData);
    }
}
