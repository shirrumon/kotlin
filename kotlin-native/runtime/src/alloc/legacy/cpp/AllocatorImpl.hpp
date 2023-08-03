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

class Allocator {
public:
    class ThreadData {
    public:
        explicit ThreadData(Allocator& allocator) noexcept
            : allocator_(allocator), objectFactoryThreadQueue_(allocator.objectFactory(), objectFactoryTraits_.CreateAllocator()),
            extraObjectDataFactoryThreadQueue_(allocator.extraObjectDataFactory()) {
        }

        Allocator& allocator() noexcept { return allocator_; }
        gc::ObjectFactory::ThreadQueue& objectFactoryThreadQueue() noexcept { return objectFactoryThreadQueue_; }
        ExtraObjectDataFactory::ThreadQueue& extraObjectDataFactoryThreadQueue() noexcept {
            return extraObjectDataFactoryThreadQueue_;
        }

        void publish() noexcept {
            extraObjectDataFactoryThreadQueue_.Publish();
            objectFactoryThreadQueue_.Publish();
        }

        void clearForTests() noexcept {
            extraObjectDataFactoryThreadQueue_.ClearForTests();
            objectFactoryThreadQueue_.ClearForTests();
        }

        ALWAYS_INLINE ObjHeader* allocateObject(const TypeInfo* typeInfo) noexcept {
            return objectFactoryThreadQueue_.CreateObject(typeInfo);
        }

        ALWAYS_INLINE ArrayHeader* allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
            return objectFactoryThreadQueue_.CreateArray(typeInfo, elements);
        }

        ALWAYS_INLINE mm::ExtraObjectData& allocateExtraObject(ObjHeader* object, const TypeInfo* typeInfo) noexcept {
            return extraObjectDataFactoryThreadQueue_.CreateExtraObjectDataForObject(object, typeInfo);
        }

        ALWAYS_INLINE void destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
            extraObject.Uninstall();
            extraObjectDataFactoryThreadQueue_.DestroyExtraObjectData(extraObject);
        }

        ALWAYS_INLINE void destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
            extraObjectDataFactoryThreadQueue_.DestroyExtraObjectData(extraObject);
        }

    private:
        Allocator& allocator_;
        [[no_unique_address]] gc::ObjectFactoryTraits objectFactoryTraits_;
        gc::ObjectFactory::ThreadQueue objectFactoryThreadQueue_;
        ExtraObjectDataFactory::ThreadQueue extraObjectDataFactoryThreadQueue_;
    };

    Allocator() noexcept : finalizerProcessor_([](int64_t epoch) {
        mm::GlobalData::Instance().gc().onFinalized(epoch);
    }) {}

    gc::ObjectFactory& objectFactory() noexcept { return objectFactory_; }
    ExtraObjectDataFactory& extraObjectDataFactory() noexcept { return extraObjectDataFactory_; }
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits>& finalizerProcessor() noexcept { return finalizerProcessor_; }

    void startFinalizerThreadIfNeeded() noexcept {
        NativeOrUnregisteredThreadGuard guard(true);
        finalizerProcessor_.StartFinalizerThreadIfNone();
        finalizerProcessor_.WaitFinalizerThreadInitialized();
    }

    void stopFinalizerThreadIfRunning() noexcept {
        NativeOrUnregisteredThreadGuard guard(true);
        finalizerProcessor_.StopFinalizerThread();
    }

    bool finalizersThreadIsRunning() noexcept {
        return finalizerProcessor_.IsRunning();
    }

    void clearForTests() noexcept {
        stopFinalizerThreadIfRunning();
        extraObjectDataFactory_.ClearForTests();
        objectFactory_.ClearForTests();
    }

private:
    gc::ObjectFactory objectFactory_;
    ExtraObjectDataFactory extraObjectDataFactory_;
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits> finalizerProcessor_;
};

}
