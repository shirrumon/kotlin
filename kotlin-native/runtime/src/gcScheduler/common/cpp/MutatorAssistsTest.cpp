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
#include "std_support/Map.hpp"

using namespace kotlin;

using gcScheduler::internal::MutatorAssists;
using Epoch = MutatorAssists::Epoch;

class MutatorAssistsTest : public ::testing::Test {
public:
    class Mutator {
    public:
        template <typename F>
        Mutator(MutatorAssistsTest& owner, F&& f) noexcept : owner_(&owner), thread_([f = std::forward<F>(f), this]() noexcept {
            ScopedMemoryInit memory;
            {
                std::unique_lock guard(initializedMutex_);
                threadData_ = memory.memoryState()->GetThreadData();
                assists_.emplace(owner_->assists_, *threadData_);
            }
            initialized_.notify_one();
            f(*this);
        }) {
            std::unique_lock guard(initializedMutex_);
            initialized_.wait(guard, [this] { return threadData_ && assists_.has_value(); });
            auto [_, inserted] = owner_->mutatorMap_.insert(std::make_pair(threadData_, this));
            RuntimeAssert(inserted, "Mutator was already inserted");
        }

        ~Mutator() {
            thread_.join();
            auto count = owner_->mutatorMap_.erase(threadData_);
            RuntimeAssert(count == 1, "Mutator must be in the map");
        }

    private:
        friend MutatorAssistsTest;

        MutatorAssistsTest* owner_ = nullptr;
        ScopedThread thread_;
        std::condition_variable initialized_;
        std::mutex initializedMutex_;
        mm::ThreadData* threadData_;
        std::optional<MutatorAssists::ThreadData> assists_;
    };

    void requestAssists(Epoch epoch) noexcept {
        assists_.requestAssists(epoch);
    }

    void completeEpoch(Epoch epoch) noexcept {
        assists_.completeEpoch(epoch, [this](mm::ThreadData& threadData) noexcept -> MutatorAssists::ThreadData& {
            return *mutatorMap_[&threadData]->assists_;
        });
    }

    void safePoint() noexcept {
        if (!mm::test_support::safePointsAreActive())
            return;
        auto* threadData = mm::ThreadRegistry::Instance().CurrentThreadData();
        mutatorMap_[threadData]->assists_->safePoint();
    }

private:
    MutatorAssists assists_;
    std_support::map<mm::ThreadData*, Mutator*> mutatorMap_;
};

TEST_F(MutatorAssistsTest, EnableSafePointsWhenRequestingAssists) {
    ASSERT_FALSE(mm::test_support::safePointsAreActive());
    requestAssists(1);
    EXPECT_TRUE(mm::test_support::safePointsAreActive());
    completeEpoch(1);
    EXPECT_FALSE(mm::test_support::safePointsAreActive());
}

TEST_F(MutatorAssistsTest, EnableSafePointsWithNestedRequest) {
    ASSERT_FALSE(mm::test_support::safePointsAreActive());
    requestAssists(1);
    ASSERT_TRUE(mm::test_support::safePointsAreActive());
    requestAssists(2);
    EXPECT_TRUE(mm::test_support::safePointsAreActive());
    completeEpoch(1);
    EXPECT_TRUE(mm::test_support::safePointsAreActive());
    completeEpoch(2);
    EXPECT_FALSE(mm::test_support::safePointsAreActive());
}

TEST_F(MutatorAssistsTest, StressEnableSafePointsByMutators) {
    constexpr Epoch epochsCount = 4;
    std::array<std::atomic<bool>, epochsCount> enabled = { false };
    std::atomic<bool> canStart = false;
    std::atomic<bool> canStop = false;
    std_support::vector<std_support::unique_ptr<Mutator>> mutators;
    for (int i = 0; i < kDefaultThreadCount; ++i) {
        mutators.emplace_back(std_support::make_unique<Mutator>(*this, [&, i](Mutator&) noexcept {
            while (!canStart.load(std::memory_order_relaxed)) {
                std::this_thread::yield();
            }
            requestAssists((i % epochsCount) + 1);
            enabled[i % epochsCount].store(true, std::memory_order_relaxed);
            while (!canStop.load(std::memory_order_relaxed)) {
                safePoint();
            }
        }));
    }

    ASSERT_FALSE(mm::test_support::safePointsAreActive());
    canStart.store(true, std::memory_order_relaxed);
    for (Epoch i = 0; i < epochsCount; ++i) {
        while (!enabled[i].load(std::memory_order_relaxed)) {
            std::this_thread::yield();
        }
        EXPECT_TRUE(mm::test_support::safePointsAreActive());
        completeEpoch(i + 1);
    }
    EXPECT_FALSE(mm::test_support::safePointsAreActive());
    canStop.store(true, std::memory_order_relaxed);
}

TEST_F(MutatorAssistsTest, Basic) {
    enum class MutatorState {
        kWaiting,
        kEngage,
        kAcceptedEngage,
        kContinue,
        kAcceptedContinue,
    };
    std::atomic<MutatorState> state = MutatorState::kWaiting;
    Mutator m(*this, [&](Mutator&) noexcept {
        while(true) {
            switch (state.load(std::memory_order_relaxed)) {
                case MutatorState::kWaiting:
                    continue;
                case MutatorState::kEngage:
                    state.store(MutatorState::kAcceptedEngage, std::memory_order_relaxed);
                    safePoint();
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
    requestAssists(1);
    state.store(MutatorState::kEngage, std::memory_order_relaxed);
    while (state.load(std::memory_order_relaxed) != MutatorState::kAcceptedEngage) {}
    state.store(MutatorState::kContinue, std::memory_order_relaxed);
    completeEpoch(1);
    while (state.load(std::memory_order_relaxed) != MutatorState::kAcceptedContinue) {}
}
