/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "GCTestSupport.hpp"

#include "AllocatorTestSupport.hpp"
#include "GCImpl.hpp"

using namespace kotlin;

void gc::AssertClear(GC& gc) noexcept {
    alloc::test_support::assertClear(gc.impl().allocator());
}
