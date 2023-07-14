/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <atomic>
#include <condition_variable>
#include <cstdint>
#include <mutex>

#include "ThreadRegistry.hpp"
#include "Utils.hpp"

namespace kotlin::gcScheduler::internal {

class MutatorAssists : private Pinned {
public:
    using Epoch = int64_t;

    class ThreadData : private Pinned {
    public:
        ThreadData(MutatorAssists& owner, mm::ThreadData& thread) noexcept : owner_(owner), thread_(thread) {}

        void safePoint() noexcept;

        std::pair<Epoch, bool> startedWaiting(std::memory_order ordering) const noexcept {
            auto value = startedWaiting_.load(ordering);
            auto waitingEpoch = value / 2;
            bool isWaiting = value % 2 == 0;
            return { waitingEpoch, isWaiting };
        }

    private:
        friend class MutatorAssists;

        bool completedEpoch(Epoch epoch) const noexcept;

        MutatorAssists& owner_;
        mm::ThreadData& thread_;
        // Contains epoch * 2. The lower bit is 1, if completed waiting.
        std::atomic<Epoch> startedWaiting_ = 1;
    };

    // Can be called multiple times per `epoch`, and `epoch` may point to the past.
    void requestAssists(Epoch epoch) noexcept;

    // Can only be called once per `epoch`, and `epoch` must be increasing
    // by exactly 1 every time.
    template <typename F>
    void completeEpoch(Epoch epoch, F&& f) noexcept {
        markEpochCompleted(epoch);
        mm::ThreadRegistry::Instance().waitAllThreads(
                [f = std::forward<F>(f), epoch](mm::ThreadData& threadData) noexcept { return f(threadData).completedEpoch(epoch); });
    }

private:
    void markEpochCompleted(Epoch epoch) noexcept;

    std::atomic<Epoch> assistsEpoch_ = 0;
    std::atomic<Epoch> completedEpoch_ = 0;
    std::mutex m_;
    std::condition_variable cv_;
};

} // namespace kotlin::gcScheduler::internal
