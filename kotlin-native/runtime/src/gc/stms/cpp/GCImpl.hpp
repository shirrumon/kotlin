/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "GC.hpp"

#include "AllocatorImpl.hpp"
#include "SameThreadMarkAndSweep.hpp"

namespace kotlin {
namespace gc {

class GC::Impl : private Pinned {
public:
    explicit Impl(gcScheduler::GCScheduler& gcScheduler) noexcept : gc_(allocator_, gcScheduler) {}

    alloc::Allocator& allocator() noexcept { return allocator_; }
    SameThreadMarkAndSweep& gc() noexcept { return gc_; }

private:
    alloc::Allocator allocator_;
    SameThreadMarkAndSweep gc_;
};

class GC::ThreadData::Impl : private Pinned {
public:
    Impl(GC& gc, mm::ThreadData& threadData) noexcept :
        allocator_(gc.impl_->allocator()) {}

    alloc::Allocator::ThreadData& allocator() noexcept { return allocator_; }

private:
    alloc::Allocator::ThreadData allocator_;
};

} // namespace gc
} // namespace kotlin
