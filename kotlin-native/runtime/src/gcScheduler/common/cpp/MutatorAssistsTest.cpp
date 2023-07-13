/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "MutatorAssists.hpp"

#include <sstream>

#include "gmock/gmock.h"
#include "gtest/gtest.h"

#include "SafePoint.hpp"
#include "TestSupport.hpp"

using namespace kotlin;

using gcScheduler::internal::MutatorAssists;

namespace {

std::string mutatorName(int id) noexcept {
    std::stringstream ss;
    ss << "Mutator#" << id;
    return ss.str();
}

class Mutator {
public:
    template <typename F>
    Mutator(MutatorAssists& assists, int id, F f) noexcept : thread_(ScopedThread::attributes().name(mutatorName(id)), [f, this, &assists]() noexcept {
        ScopedMemoryInit memory;
        {
            std::unique_lock guard(initializedMutex_);
            threadData_ = memory.memoryState()->GetThreadData();
            assists_.emplace(assists, *threadData_);
        }
        initialized_.notify_one();
        f(*assists_);
    }) {
        std::unique_lock guard(initializedMutex_);
        initialized_.wait(guard, [this] { return threadData_ && assists_.has_value(); });
    }

    mm::ThreadData& threadData() noexcept { return *threadData_; }
    MutatorAssists::ThreadData& assists() noexcept { return *assists_; }

private:
    ScopedThread thread_;
    std::condition_variable initialized_;
    std::mutex initializedMutex_;
    mm::ThreadData* threadData_;
    std::optional<MutatorAssists::ThreadData> assists_;
};

}

TEST(MutatorAssistsTest, Basic) {
    MutatorAssists assists;
    enum class MutatorState {
        kWaiting,
        kEngage,
        kAcceptedEngage,
        kContinue,
        kAcceptedContinue,
    };
    std::atomic<MutatorState> state = MutatorState::kWaiting;
    Mutator m(assists, 0, [&state](MutatorAssists::ThreadData& thread) noexcept {
        while(true) {
            switch (state.load(std::memory_order_relaxed)) {
                case MutatorState::kWaiting:
                    continue;
                case MutatorState::kEngage:
                    state.store(MutatorState::kAcceptedEngage, std::memory_order_relaxed);
                    thread.safePoint();
                    ASSERT_THAT(state.load(std::memory_order_relaxed), MutatorState::kContinue);
                    state.store(MutatorState::kAcceptedContinue, std::memory_order_relaxed);
                    continue;
                case MutatorState::kAcceptedContinue:
                    return;
                case MutatorState::kAcceptedEngage:
                case MutatorState::kContinue:
                    GTEST_FAIL();
                    break;
            }
        }
    });
    assists.requestAssists(1);
    state.store(MutatorState::kEngage, std::memory_order_relaxed);
    while (state.load(std::memory_order_relaxed) != MutatorState::kAcceptedEngage) {}
    state.store(MutatorState::kContinue, std::memory_order_relaxed);
    assists.completeEpoch(1, [&](mm::ThreadData& threadData) noexcept -> MutatorAssists::ThreadData& {
        EXPECT_THAT(threadData, testing::Ref(m.threadData()));
        return m.assists();
    });
    while (state.load(std::memory_order_relaxed) != MutatorState::kAcceptedContinue) {}
}
