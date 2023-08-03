/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "Allocator.hpp"
#include "ExtraObjectDataFactory.hpp"
#include "FinalizerProcessor.hpp"
#include "GC.hpp"
#include "GlobalData.hpp"
#include "Logging.hpp"
#include "ObjectFactory.hpp"
#include "ObjectFactoryAllocator.hpp"
#include "ObjectFactorySweep.hpp"
#include "ThreadData.hpp"

namespace kotlin::gc {

struct ObjectFactoryTraits {
    using Allocator = alloc::AllocatorWithGC<alloc::AllocatorBasic, ObjectFactoryTraits>;
    using ObjectData = gc::GC::ObjectData;

    Allocator CreateAllocator() noexcept { return Allocator(alloc::AllocatorBasic(), *this); }

    void OnOOM(size_t size) noexcept {
        RuntimeLogDebug({kTagGC}, "Attempt to GC on OOM at size=%zu", size);
        // TODO: This will print the log for "manual" scheduling. Fix this.
        mm::GlobalData::Instance().gcScheduler().scheduleAndWaitFinished();
    }
};

using ObjectFactory = alloc::ObjectFactory<ObjectFactoryTraits>;


} // namespace kotlin::gc

namespace kotlin::alloc {

using FinalizerQueue = gc::ObjectFactory::FinalizerQueue;
using FinalizerQueueTraits = gc::ObjectFactory::FinalizerQueueTraits;

class Allocator::Impl : private Pinned {
public:
    Impl() noexcept : finalizerProcessor_([](int64_t epoch) {
        mm::GlobalData::Instance().gc().onFinalized(epoch);
    }) {}

    gc::ObjectFactory& objectFactory() noexcept { return objectFactory_; }
    ExtraObjectDataFactory& extraObjectDataFactory() noexcept { return extraObjectDataFactory_; }
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits>& finalizerProcessor() noexcept { return finalizerProcessor_; }

private:
    gc::ObjectFactory objectFactory_;
    ExtraObjectDataFactory extraObjectDataFactory_;
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits> finalizerProcessor_;
};

class Allocator::ThreadData::Impl {
public:
    explicit Impl(Allocator::Impl& allocator) noexcept
        : allocator_(allocator), objectFactoryThreadQueue_(allocator.objectFactory(), objectFactoryTraits_.CreateAllocator()),
        extraObjectDataFactoryThreadQueue_(allocator.extraObjectDataFactory()) {
    }

    Allocator::Impl& allocator() noexcept { return allocator_; }
    gc::ObjectFactory::ThreadQueue& objectFactoryThreadQueue() noexcept { return objectFactoryThreadQueue_; }
    ExtraObjectDataFactory::ThreadQueue& extraObjectDataFactoryThreadQueue() noexcept {
        return extraObjectDataFactoryThreadQueue_;
    }

private:
    Allocator::Impl& allocator_;
    [[no_unique_address]] gc::ObjectFactoryTraits objectFactoryTraits_;
    gc::ObjectFactory::ThreadQueue objectFactoryThreadQueue_;
    ExtraObjectDataFactory::ThreadQueue extraObjectDataFactoryThreadQueue_;
};

}
