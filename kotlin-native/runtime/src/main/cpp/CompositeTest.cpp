/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "Composite.hpp"

#include "gmock/gmock.h"
#include "gtest/gtest.h"

#include "std_support/CStdlib.hpp"

using namespace kotlin;

namespace {

class HookedCtorDtor : private Pinned {
public:
    HookedCtorDtor() noexcept { ctorHook_->Call(this); }

    ~HookedCtorDtor() { dtorHook_->Call(this); }

    static testing::MockFunction<void(void*)>*& ctorHook() noexcept { return ctorHook_; }
    static testing::MockFunction<void(void*)>*& dtorHook() noexcept { return dtorHook_; }

private:
    static testing::MockFunction<void(void*)>* ctorHook_;
    static testing::MockFunction<void(void*)>* dtorHook_;
};

// static
testing::MockFunction<void(void*)>* HookedCtorDtor::ctorHook_ = nullptr;

// static
testing::MockFunction<void(void*)>* HookedCtorDtor::dtorHook_ = nullptr;

} // namespace

class CompositeTest : public ::testing::Test {
public:
    CompositeTest() {
        HookedCtorDtor::ctorHook() = &ctorHook_;
        HookedCtorDtor::dtorHook() = &dtorHook_;
    }

    ~CompositeTest() {
        HookedCtorDtor::ctorHook() = nullptr;
        HookedCtorDtor::dtorHook() = nullptr;
    }

    testing::MockFunction<void(void*)>& ctorHook() noexcept { return ctorHook_; }
    testing::MockFunction<void(void*)>& dtorHook() noexcept { return dtorHook_; }

private:
    testing::StrictMock<testing::MockFunction<void(void*)>> ctorHook_;
    testing::StrictMock<testing::MockFunction<void(void*)>> dtorHook_;
};

static_assert(Single<int32_t>::objectSize() == sizeof(int32_t));
static_assert(Single<int32_t>::objectAlignment() == alignof(int32_t));
static_assert(Single<int32_t, alignof(int64_t)>::objectSize() == sizeof(int64_t));
static_assert(Single<int32_t, alignof(int64_t)>::objectAlignment() == alignof(int64_t));

TEST_F(CompositeTest, SingleGet) {
    int32_t value = 42;
    auto* single = Single<int32_t>::reinterpret(&value);
    EXPECT_THAT(&single->get<0>(), &value);
}

TEST_F(CompositeTest, SingleGetOverAligned) {
    alignas(int64_t) int32_t value = 42;
    auto* single = Single<int32_t, alignof(int64_t)>::reinterpret(&value);
    EXPECT_THAT(&single->get<0>(), &value);
}

TEST_F(CompositeTest, ConstructorDestructor) {
    using T = Single<HookedCtorDtor>;
    void* ptr = std_support::aligned_malloc(T::objectAlignment(), T::objectSize());
    auto* single = T::reinterpret(ptr);

    EXPECT_CALL(ctorHook(), Call(ptr));
    single->construct();
    testing::Mock::VerifyAndClear(&ctorHook());
    testing::Mock::VerifyAndClear(&dtorHook());

    EXPECT_CALL(dtorHook(), Call(ptr));
    single->destroy();
    testing::Mock::VerifyAndClear(&ctorHook());
    testing::Mock::VerifyAndClear(&dtorHook());

    std_support::aligned_free(ptr);
}

static_assert(Composite<Single<int32_t>>::objectSize() == sizeof(int32_t));
static_assert(Composite<Single<int32_t>>::objectAlignment() == alignof(int32_t));
static_assert(Composite<Single<int32_t>, Single<int32_t>, Single<int32_t>>::objectSize() == 3 * sizeof(int32_t));
static_assert(Composite<Single<int32_t>, Single<int32_t>, Single<int32_t>>::objectAlignment() == alignof(int32_t));
static_assert(Composite<Single<int64_t>, Single<int32_t>, Single<int32_t>>::objectSize() == 2 * sizeof(int64_t));
static_assert(Composite<Single<int64_t>, Single<int32_t>, Single<int32_t>>::objectAlignment() == alignof(int64_t));
static_assert(Composite<Single<int32_t>, Single<int64_t>, Single<int32_t>>::objectSize() == 3 * sizeof(int64_t));
static_assert(Composite<Single<int32_t>, Single<int64_t>, Single<int32_t>>::objectAlignment() == alignof(int64_t));
static_assert(Composite<Single<int32_t>, Single<int32_t>, Single<int64_t>>::objectSize() == 2 * sizeof(int64_t));
static_assert(Composite<Single<int32_t>, Single<int32_t>, Single<int64_t>>::objectAlignment() == alignof(int64_t));
