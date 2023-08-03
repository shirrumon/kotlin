/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "GC.hpp"

#include "AllocatorImpl.hpp"
#include "ConcurrentMarkAndSweep.hpp"

namespace kotlin {
namespace gc {

class GC::Impl : private Pinned {
public:
    explicit Impl(gcScheduler::GCScheduler& gcScheduler) noexcept : gc_(allocator_, gcScheduler, compiler::gcMutatorsCooperate(), compiler::auxGCThreads()) {}

    alloc::Allocator& allocator() noexcept { return allocator_; }
    ConcurrentMarkAndSweep& gc() noexcept { return gc_; }

private:
    alloc::Allocator allocator_;
    ConcurrentMarkAndSweep gc_;
};

class GC::ThreadData::Impl : private Pinned {
public:
    Impl(GC& gc, mm::ThreadData& threadData) noexcept :
        allocator_(gc.impl_->allocator()),
        gc_(gc.impl_->gc(), threadData) {}

    alloc::Allocator::ThreadData& allocator() noexcept { return allocator_; }
    ConcurrentMarkAndSweep::ThreadData& gc() noexcept { return gc_; }

private:
    alloc::Allocator::ThreadData allocator_;
    ConcurrentMarkAndSweep::ThreadData gc_;
};

} // namespace gc
} // namespace kotlin
