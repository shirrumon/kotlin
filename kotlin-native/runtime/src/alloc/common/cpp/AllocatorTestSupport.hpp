/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "Allocator.hpp"
#include "std_support/Vector.hpp"

namespace kotlin::alloc::test_support {

void assertClear(Allocator& allocator) noexcept;
std_support::vector<ObjHeader*> allocatedObjects(Allocator::ThreadData& allocator) noexcept;

extern const bool hasPerThreadLiveness;

}
