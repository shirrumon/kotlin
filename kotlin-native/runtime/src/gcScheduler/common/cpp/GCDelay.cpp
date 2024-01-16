/*
* Copyright 2010-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "GCDelay.hpp"

#include "GCStatistics.hpp"

using namespace kotlin;

void gcScheduler::GCDelay::allowGC() noexcept {
    std::unique_lock guard{mutex_};
    --disallowCounter_;
    auto canAllow = disallowCounter_ == 0;
    guard.unlock();
    if (canAllow) {
        cv_.notify_all();
    }
}

void gcScheduler::GCDelay::disallowGC() noexcept {
    std::unique_lock guard{mutex_};
    ++disallowCounter_;
    // Ideally, this should not happen, but protect just in case.
    auto canAllow = disallowCounter_ == 0;
    guard.unlock();
    if (canAllow) {
        cv_.notify_all();
    }
}

void gcScheduler::GCDelay::waitGCAllowed(int64_t epoch, std::chrono::microseconds maxDuration) noexcept {
    std::unique_lock guard{mutex_};
    auto canStop = [&]() noexcept { return disallowCounter_ == 0; };
    if (canStop()) {
        // Nothing to wait for.
        return;
    }
    if (lastAssistEpoch_ >= epoch) {
        // Some assist has succeeded already, must not wait.
        return;
    }
    lastGCWaitingEpoch_ = epoch;
    GCLogDebug(epoch, "In GC delay zone. Waiting to start GC for maximum of %" PRId64 " us", std::chrono::microseconds(maxDuration).count());
    bool gcAllowed = cv_.wait_for(guard, maxDuration, canStop());
    lastGCAllowedEpoch_ = epoch;
    if (!gcAllowed) {
        GCLogWarning(epoch, "In GC delay zone. Timed out waiting to start GC");
    }
}

bool gcScheduler::GCDelay::tryGCAssist(int64_t epoch) noexcept {
    std::unique_lock guard{mutex_};
    if (lastGCWaitingEpoch_ == epoch && lastGCAllowedEpoch_ != epoch) {
        // GC is waiting, cannot assist.
        return false;
    }
    lastAssistEpoch_ = epoch;
    return true;
}