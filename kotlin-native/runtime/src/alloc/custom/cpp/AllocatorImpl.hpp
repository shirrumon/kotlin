/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "Allocator.hpp"
#include "CustomAllocator.hpp"
#include "CustomFinalizerProcessor.hpp"
#include "ExtraObjectData.hpp"
#include "FinalizerProcessor.hpp"
#include "GCApi.hpp"
#include "Heap.hpp"

namespace kotlin::alloc {

class Allocator {
public:
    class ThreadData {
    public:
        explicit ThreadData(Allocator& allocator) noexcept
            : allocator_(allocator), alloc_(allocator.heap()) {}

        Allocator& allocator() noexcept { return allocator_; }
        CustomAllocator& alloc() noexcept { return alloc_; }

        void publish() noexcept {}

        void clearForTests() noexcept {
            alloc_.PrepareForGC();
        }

        ALWAYS_INLINE ObjHeader* allocateObject(const TypeInfo* typeInfo) noexcept {
            return alloc_.CreateObject(typeInfo);
        }

        ALWAYS_INLINE ArrayHeader* allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
            return alloc_.CreateArray(typeInfo, elements);
        }

        ALWAYS_INLINE mm::ExtraObjectData& allocateExtraObject(ObjHeader* object, const TypeInfo* typeInfo) noexcept {
            return alloc_.CreateExtraObjectDataForObject(object, typeInfo);
        }

        ALWAYS_INLINE void destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
            extraObject.ReleaseAssociatedObject();
            extraObject.setFlag(mm::ExtraObjectData::FLAGS_FINALIZED);
        }

        ALWAYS_INLINE void destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
            extraObject.setFlag(mm::ExtraObjectData::FLAGS_SWEEPABLE);
        }

    private:
        Allocator& allocator_;
        CustomAllocator alloc_;
    };

    Allocator() noexcept : finalizerProcessor_([](int64_t epoch) {
        mm::GlobalData::Instance().gc().onFinalized(epoch);
    }) {}

    Heap& heap() noexcept { return heap_; }
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
        heap_.ClearForTests();
    }

private:
    Heap heap_;
    FinalizerProcessor<FinalizerQueue, FinalizerQueueTraits> finalizerProcessor_;
};

}
