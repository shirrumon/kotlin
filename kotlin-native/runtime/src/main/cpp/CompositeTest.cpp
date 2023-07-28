/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "Composite.hpp"

#include "gmock/gmock.h"
#include "gtest/gtest.h"

using namespace kotlin;

namespace {

struct TEmpty {};

using CEmpty = composite::Composite<>;

struct T323232 {
    int32_t f1;
    int32_t f2;
    int32_t f3;
};

using C323232 = composite::Composite<composite::Reg<int32_t>, composite::Reg<int32_t>, composite::Reg<int32_t>>;

struct T643232 {
    int64_t f1;
    int32_t f2;
    int32_t f3;
};

using C643232 = composite::Composite<composite::Reg<int64_t>, composite::Reg<int32_t>, composite::Reg<int32_t>>;

struct T326432 {
    int32_t f1;
    int64_t f2;
    int32_t f3;
};

using C326432 = composite::Composite<composite::Reg<int32_t>, composite::Reg<int64_t>, composite::Reg<int32_t>>;

struct T323264 {
    int32_t f1;
    int32_t f2;
    int64_t f3;
};

using C323264 = composite::Composite<composite::Reg<int32_t>, composite::Reg<int32_t>, composite::Reg<int64_t>>;

struct TEmpty326432 {
    [[no_unique_address]] TEmpty f1;
    int32_t f2;
    int64_t f3;
    int32_t f4;
};

using CEmpty326432 = composite::Composite<CEmpty, composite::Reg<int32_t>, composite::Reg<int64_t>, composite::Reg<int32_t>>;

struct T32Empty6432 {
    int32_t f1;
    [[no_unique_address]] TEmpty f2;
    int64_t f3;
    int32_t f4;
};

using C32Empty6432 = composite::Composite<composite::Reg<int32_t>, CEmpty, composite::Reg<int64_t>, composite::Reg<int32_t>>;

struct T3264Empty32 {
    int32_t f1;
    int64_t f2;
    [[no_unique_address]] TEmpty f3;
    int32_t f4;
};

using C3264Empty32 = composite::Composite<composite::Reg<int32_t>, composite::Reg<int64_t>, CEmpty, composite::Reg<int32_t>>;

struct T326432Empty {
    int32_t f1;
    int64_t f2;
    int32_t f3;
    [[no_unique_address]] TEmpty f4;
};

using C326432Empty = composite::Composite<composite::Reg<int32_t>, composite::Reg<int64_t>, composite::Reg<int32_t>, CEmpty>;

} // namespace

// Empty
static_assert(CEmpty().fieldOffset<0>() == 0);
static_assert(CEmpty().size() == 0);
static_assert(CEmpty().alignment() == alignof(TEmpty));

// int32_t, int32_t, int32_t
static_assert(C323232().fieldOffset<0>() == offsetof(T323232, f1));
static_assert(C323232().fieldOffset<1>() == offsetof(T323232, f2));
static_assert(C323232().fieldOffset<2>() == offsetof(T323232, f3));
static_assert(C323232().size() == sizeof(T323232));
static_assert(C323232().alignment() == alignof(T323232));

// int64_t, int32_t, int32_t
static_assert(C643232().fieldOffset<0>() == offsetof(T643232, f1));
static_assert(C643232().fieldOffset<1>() == offsetof(T643232, f2));
static_assert(C643232().fieldOffset<2>() == offsetof(T643232, f3));
static_assert(C643232().size() == sizeof(T643232));
static_assert(C643232().alignment() == alignof(T643232));

// int32_t, int64_t, int32_t
static_assert(C326432().fieldOffset<0>() == offsetof(T326432, f1));
static_assert(C326432().fieldOffset<1>() == offsetof(T326432, f2));
static_assert(C326432().fieldOffset<2>() == offsetof(T326432, f3));
static_assert(C326432().size() == sizeof(T326432));
static_assert(C326432().alignment() == alignof(T326432));

// int32_t, int32_t, int64_t
static_assert(C323264().fieldOffset<0>() == offsetof(T323264, f1));
static_assert(C323264().fieldOffset<1>() == offsetof(T323264, f2));
static_assert(C323264().fieldOffset<2>() == offsetof(T323264, f3));
static_assert(C323264().size() == sizeof(T323264));
static_assert(C323264().alignment() == alignof(T323264));

// Empty, int32_t, int64_t, int32_t
// Pointer into the empty field differs from C++ structs.
static_assert(CEmpty326432().fieldOffset<0>() == 0);
static_assert(CEmpty326432().fieldOffset<1>() == offsetof(TEmpty326432, f2));
static_assert(CEmpty326432().fieldOffset<2>() == offsetof(TEmpty326432, f3));
static_assert(CEmpty326432().fieldOffset<3>() == offsetof(TEmpty326432, f4));
static_assert(CEmpty326432().size() == sizeof(TEmpty326432));
static_assert(CEmpty326432().alignment() == alignof(TEmpty326432));

// int32_t, Empty, int64_t, int32_t
static_assert(C32Empty6432().fieldOffset<0>() == offsetof(T32Empty6432, f1));
// Pointer into the empty field differs from C++ structs.
static_assert(C32Empty6432().fieldOffset<1>() == sizeof(int32_t));
static_assert(C32Empty6432().fieldOffset<2>() == offsetof(T32Empty6432, f3));
static_assert(C32Empty6432().fieldOffset<3>() == offsetof(T32Empty6432, f4));
static_assert(C32Empty6432().size() == sizeof(T32Empty6432));
static_assert(C32Empty6432().alignment() == alignof(T32Empty6432));

// int32_t, int64_t, Empty, int32_t
static_assert(C3264Empty32().fieldOffset<0>() == offsetof(T3264Empty32, f1));
static_assert(C3264Empty32().fieldOffset<1>() == offsetof(T3264Empty32, f2));
// Pointer into the empty field differs from C++ structs.
static_assert(C3264Empty32().fieldOffset<2>() == AlignUp(sizeof(int32_t), alignof(int64_t)) + sizeof(int64_t));
static_assert(C3264Empty32().fieldOffset<3>() == offsetof(T3264Empty32, f4));
static_assert(C3264Empty32().size() == sizeof(T3264Empty32));
static_assert(C3264Empty32().alignment() == alignof(T3264Empty32));

// int32_t, int64_t, int32_t, Empty
static_assert(C326432Empty().fieldOffset<0>() == offsetof(T326432Empty, f1));
static_assert(C326432Empty().fieldOffset<1>() == offsetof(T326432Empty, f2));
static_assert(C326432Empty().fieldOffset<2>() == offsetof(T326432Empty, f3));
// Pointer into the empty field differs from C++ structs.
static_assert(C326432Empty().fieldOffset<3>() == AlignUp(sizeof(int32_t), alignof(int64_t)) + sizeof(int64_t) + sizeof(int32_t));
static_assert(C326432Empty().size() == sizeof(T326432Empty));
static_assert(C326432Empty().alignment() == alignof(T326432Empty));

TEST(CompositeTest, VLA) {
    composite::Dynamic dyn(3 * sizeof(void*), alignof(void*));
    composite::Composite comp(composite::Reg<int32_t>(), dyn);
    EXPECT_THAT(comp.fieldOffset<0>(), 0);
    EXPECT_THAT(comp.fieldOffset<1>(), sizeof(void*));
    EXPECT_THAT(comp.size(), 4 * sizeof(void*));
    EXPECT_THAT(comp.alignment(), alignof(void*));
}
