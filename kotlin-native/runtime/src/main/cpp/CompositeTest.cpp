/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "Composite.hpp"

#include "gmock/gmock.h"
#include "gtest/gtest.h"

using namespace kotlin;

namespace {

template <typename T, size_t alignAs = alignof(T)>
struct Reg {
    static_assert(IsValidAlignment(alignAs));
    static_assert(alignAs >= alignof(T));

    static constexpr size_t size() noexcept { return sizeof(T); }
    static constexpr size_t alignment() noexcept { return alignAs; }
};

struct Empty {
    static constexpr size_t size() noexcept { return 0; }
    static constexpr size_t alignment() noexcept { return 1; }
};

class Dynamic {
public:
    Dynamic(size_t size, size_t alignment) : size_(size), alignment_(alignment) {}

    size_t size() noexcept { return size_; }
    size_t alignment() noexcept { return alignment_; }

private:
    size_t size_;
    size_t alignment_;
};

struct T323232 {
    int32_t f1;
    int32_t f2;
    int32_t f3;
};

struct T643232 {
    int64_t f1;
    int32_t f2;
    int32_t f3;
};

struct T326432 {
    int32_t f1;
    int64_t f2;
    int32_t f3;
};

struct T323264 {
    int32_t f1;
    int32_t f2;
    int64_t f3;
};

struct TEmpty {};

struct TEmpty326432 {
    [[no_unique_address]] TEmpty f1;
    int32_t f2;
    int64_t f3;
    int32_t f4;
};

struct T32Empty6432 {
    int32_t f1;
    [[no_unique_address]] TEmpty f2;
    int64_t f3;
    int32_t f4;
};

struct T3264Empty32 {
    int32_t f1;
    int64_t f2;
    [[no_unique_address]] TEmpty f3;
    int32_t f4;
};

struct T326432Empty {
    int32_t f1;
    int64_t f2;
    int32_t f3;
    [[no_unique_address]] TEmpty f4;
};

}

// Empty
static_assert(composite::fieldOffset<0>() == 0);
static_assert(composite::size() == 0);
static_assert(composite::alignment() == 1);

// int32_t, int32_t, int32_t
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T323232, f1));
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T323232, f2));
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T323232, f3));
static_assert(composite::size(Reg<int32_t>(), Reg<int32_t>(), Reg<int32_t>()) == sizeof(T323232));
static_assert(composite::alignment(Reg<int32_t>(), Reg<int32_t>(), Reg<int32_t>()) == alignof(T323232));

// int64_t, int32_t, int32_t
static_assert(composite::fieldOffset<0>(Reg<int64_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T643232, f1));
static_assert(composite::fieldOffset<1>(Reg<int64_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T643232, f2));
static_assert(composite::fieldOffset<2>(Reg<int64_t>(), Reg<int32_t>(), Reg<int32_t>()) == offsetof(T643232, f3));
static_assert(composite::size(Reg<int64_t>(), Reg<int32_t>(), Reg<int32_t>()) == sizeof(T643232));
static_assert(composite::alignment(Reg<int64_t>(), Reg<int32_t>(), Reg<int32_t>()) == alignof(T643232));

// int32_t, int64_t, int32_t
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T326432, f1));
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T326432, f2));
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T326432, f3));
static_assert(composite::size(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == sizeof(T326432));
static_assert(composite::alignment(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == alignof(T326432));

// int32_t, int32_t, int64_t
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Reg<int32_t>(), Reg<int64_t>()) == offsetof(T323264, f1));
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Reg<int32_t>(), Reg<int64_t>()) == offsetof(T323264, f2));
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Reg<int32_t>(), Reg<int64_t>()) == offsetof(T323264, f3));
static_assert(composite::size(Reg<int32_t>(), Reg<int32_t>(), Reg<int64_t>()) == sizeof(T323264));
static_assert(composite::alignment(Reg<int32_t>(), Reg<int32_t>(), Reg<int64_t>()) == alignof(T323264));

// Empty, int32_t, int64_t, int32_t
// Pointer into the empty field differs from C++ structs.
static_assert(composite::fieldOffset<0>(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == 0);
static_assert(composite::fieldOffset<1>(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(TEmpty326432, f2));
static_assert(composite::fieldOffset<2>(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(TEmpty326432, f3));
static_assert(composite::fieldOffset<3>(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(TEmpty326432, f4));
static_assert(composite::size(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == sizeof(TEmpty326432));
static_assert(composite::alignment(Empty(), Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>()) == alignof(TEmpty326432));

// int32_t, Empty, int64_t, int32_t
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T32Empty6432, f1));
// Pointer into the empty field differs from C++ structs.
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == sizeof(int32_t));
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T32Empty6432, f3));
static_assert(composite::fieldOffset<3>(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == offsetof(T32Empty6432, f4));
static_assert(composite::size(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == sizeof(T32Empty6432));
static_assert(composite::alignment(Reg<int32_t>(), Empty(), Reg<int64_t>(), Reg<int32_t>()) == alignof(T32Empty6432));

// int32_t, int64_t, Empty, int32_t
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == offsetof(T3264Empty32, f1));
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == offsetof(T3264Empty32, f2));
// Pointer into the empty field differs from C++ structs.
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == AlignUp(sizeof(int32_t), alignof(int64_t)) + sizeof(int64_t));
static_assert(composite::fieldOffset<3>(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == offsetof(T3264Empty32, f4));
static_assert(composite::size(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == sizeof(T3264Empty32));
static_assert(composite::alignment(Reg<int32_t>(), Reg<int64_t>(), Empty(), Reg<int32_t>()) == alignof(T3264Empty32));

// int32_t, int64_t, int32_t, Empty
static_assert(composite::fieldOffset<0>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == offsetof(T326432Empty, f1));
static_assert(composite::fieldOffset<1>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == offsetof(T326432Empty, f2));
static_assert(composite::fieldOffset<2>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == offsetof(T326432Empty, f3));
// Pointer into the empty field differs from C++ structs.
static_assert(composite::fieldOffset<3>(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == AlignUp(sizeof(int32_t), alignof(int64_t)) + sizeof(int64_t) + sizeof(int32_t));
static_assert(composite::size(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == sizeof(T326432Empty));
static_assert(composite::alignment(Reg<int32_t>(), Reg<int64_t>(), Reg<int32_t>(), Empty()) == alignof(T326432Empty));

TEST(CompositeTest, VLA) {
    Dynamic dyn(3 * sizeof(void*), alignof(void*));
    EXPECT_THAT(composite::fieldOffset<0>(Reg<int32_t>(), dyn), 0);
    EXPECT_THAT(composite::fieldOffset<1>(Reg<int32_t>(), dyn), sizeof(void*));
    EXPECT_THAT(composite::size(Reg<int32_t>(), dyn), 4 * sizeof(void*));
    EXPECT_THAT(composite::alignment(Reg<int32_t>(), dyn), alignof(void*));
}
