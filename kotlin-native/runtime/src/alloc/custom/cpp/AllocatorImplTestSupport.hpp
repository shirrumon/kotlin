/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "AllocatorImpl.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

namespace {

template <typename T>
auto collectCopy(T& iterable) {
    std::vector<std::remove_reference_t<decltype(*iterable.begin())>> result;
    for (const auto& element : iterable) {
        result.push_back(element);
    }
    return result;
}

}

namespace kotlin::alloc::test_support {

inline void assertClear(Allocator& allocator) noexcept {
    auto objects = allocator.heap().GetAllocatedObjects();
    EXPECT_THAT(collectCopy(objects), testing::UnorderedElementsAre());
}

inline std_support::vector<ObjHeader*> allocatedObjects(Allocator::ThreadData& allocator) noexcept {
    return allocator.allocator().heap().GetAllocatedObjects();
}

inline constexpr bool hasPerThreadLiveness = false;

}
