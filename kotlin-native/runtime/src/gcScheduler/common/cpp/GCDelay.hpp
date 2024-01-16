/*
* Copyright 2010-2024 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <chrono>
#include <condition_variable>
#include <mutex>

#include "Utils.hpp"

namespace kotlin::gcScheduler {

class GCDelay : private Pinned {
public:
    GCDelay() noexcept = default;

    void allowGC() noexcept;
    void disallowGC() noexcept;

    void waitGCAllowed(int64_t epoch, std::chrono::microseconds maxDuration) noexcept;
    bool tryGCAssist(int64_t epoch) noexcept;

private:
    std::mutex mutex_;
    std::condition_variable cv_;
    int64_t disallowCounter_ = 0;
    int64_t lastAssistEpoch_ = 0;
    int64_t lastGCWaitingEpoch_ = 0;
    int64_t lastGCAllowedEpoch_ = 0;
};

}