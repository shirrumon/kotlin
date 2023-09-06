/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "Allocator.hpp"

#include "CustomAllocator.hpp"
#include "CustomFinalizerProcessor.hpp"
#include "FinalizerProcessor.hpp"
#include "GCApi.hpp"
#include "GlobalData.hpp"
#include "Heap.hpp"

namespace kotlin::alloc {

class Allocator::Impl : private Pinned {
public:
    Impl() noexcept : finalizerProcessor_([](int64_t epoch) noexcept { mm::GlobalData::Instance().gc().onFinalized(epoch); }) {}

    Heap& heap() noexcept { return heap_; }

    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits>& finalizerProcessor() noexcept { return finalizerProcessor_; }

private:
    Heap heap_;
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits> finalizerProcessor_;
};

class Allocator::ThreadData::Impl : private Pinned {
public:
    explicit Impl(Allocator::Impl& allocator) noexcept : alloc_(allocator.heap()) {}

    alloc::CustomAllocator& alloc() noexcept { return alloc_; }

private:
    CustomAllocator alloc_;
};

} // namespace kotlin::alloc
