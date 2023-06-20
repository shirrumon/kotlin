#include "SafePoint.hpp"
#include "ThreadData.hpp"
#include "KAssert.h"

namespace {

std::atomic<kotlin::mm::SafePointAction> safePointAction = nullptr;

} // namespace

bool kotlin::mm::TrySetSafePointAction(kotlin::mm::SafePointAction action) noexcept {
    kotlin::mm::SafePointAction expected = nullptr;
    kotlin::mm::SafePointAction desired = action;
    return safePointAction.compare_exchange_strong(expected, desired, std::memory_order_seq_cst);
}

void kotlin::mm::UnsetSafePointAction() noexcept {
    RuntimeAssert(kotlin::mm::IsSafePointActionRequested(), "Some safe point action must be set");
    safePointAction.store(nullptr, std::memory_order_seq_cst);
}

bool kotlin::mm::IsSafePointActionRequested() noexcept {
    return safePointAction.load(std::memory_order_relaxed) != nullptr;
}

ALWAYS_INLINE void kotlin::mm::SafePoint(mm::ThreadData& threadData) noexcept {
    // TODO nullable action VS flag + action call
    auto action = safePointAction.load(std::memory_order_relaxed);
    if (action != nullptr) /*[[unlikely]]*/ {
        action(threadData);
    }
}
