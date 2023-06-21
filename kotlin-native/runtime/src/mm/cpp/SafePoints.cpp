#include "SafePoint.hpp"
#include "ThreadData.hpp"
#include "KAssert.h"

namespace {

void NoOp(kotlin::mm::ThreadData*) noexcept {}
std::atomic<kotlin::mm::SafePointAction> safePointAction = NoOp;

} // namespace

bool kotlin::mm::TrySetSafePointAction(kotlin::mm::SafePointAction action) noexcept {
    kotlin::mm::SafePointAction expected = NoOp;
    kotlin::mm::SafePointAction desired = action;
    return safePointAction.compare_exchange_strong(expected, desired, std::memory_order_release, std::memory_order_acquire);
}

void kotlin::mm::UnsetSafePointAction() noexcept {
    RuntimeAssert(kotlin::mm::IsSafePointActionRequested(), "Some safe point action must be set");
    safePointAction.store(NoOp, std::memory_order_release);
}

bool kotlin::mm::IsSafePointActionRequested() noexcept {
    return safePointAction.load(std::memory_order_relaxed) != nullptr;
}

ALWAYS_INLINE void kotlin::mm::SafePoint() noexcept {
    safePointAction.load(std::memory_order_acquire)(nullptr);
}

void kotlin::mm::SafePoint(mm::ThreadData& threadData) noexcept {
    safePointAction.load(std::memory_order_acquire)(&threadData);
}
