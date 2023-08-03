/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

#include "GCApi.hpp"

using namespace kotlin;

alloc::Allocator::ThreadData::ThreadData(Allocator& allocator) noexcept
    : impl_(std_support::make_unique<Impl>(allocator.impl())) {}

alloc::Allocator::ThreadData::~ThreadData() = default;

void alloc::Allocator::ThreadData::publish() noexcept {}

void alloc::Allocator::ThreadData::clearForTests() noexcept {
    impl().alloc().PrepareForGC();
}

ALWAYS_INLINE ObjHeader* alloc::Allocator::ThreadData::allocateObject(const TypeInfo* typeInfo) noexcept {
    return impl().alloc().CreateObject(typeInfo);
}

ALWAYS_INLINE ArrayHeader* alloc::Allocator::ThreadData::allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
    return impl().alloc().CreateArray(typeInfo, elements);
}

ALWAYS_INLINE mm::ExtraObjectData& alloc::Allocator::ThreadData::allocateExtraObject(ObjHeader* object, const TypeInfo* typeInfo) noexcept {
    return impl().alloc().CreateExtraObjectDataForObject(object, typeInfo);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.ReleaseAssociatedObject();
    extraObject.setFlag(mm::ExtraObjectData::FLAGS_FINALIZED);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.setFlag(mm::ExtraObjectData::FLAGS_SWEEPABLE);
}

alloc::Allocator::Allocator() noexcept
    : impl_(std_support::make_unique<Impl>()) {}

alloc::Allocator::~Allocator() = default;

void alloc::Allocator::clearForTests() noexcept {
    stopFinalizerThreadIfRunning();
    impl().heap().ClearForTests();
}

void alloc::Allocator::startFinalizerThreadIfNeeded() noexcept {
    NativeOrUnregisteredThreadGuard guard(true);
    impl().finalizerProcessor().StartFinalizerThreadIfNone();
    impl().finalizerProcessor().WaitFinalizerThreadInitialized();
}

void alloc::Allocator::stopFinalizerThreadIfRunning() noexcept {
    NativeOrUnregisteredThreadGuard guard(true);
    impl().finalizerProcessor().StopFinalizerThread();
}

bool alloc::Allocator::finalizersThreadIsRunning() noexcept {
    return impl().finalizerProcessor().IsRunning();
}

void alloc::initObjectPool() noexcept {}

void alloc::compactObjectPoolInCurrentThread() noexcept {}

size_t alloc::allocatedBytes() noexcept {
    return GetAllocatedBytes();
}

size_t alloc::allocatedHeapSize(ObjHeader* object) noexcept {
    RuntimeAssert(object->heap(), "Object must be a heap object");
    const auto* typeInfo = object->type_info();
    if (typeInfo->IsArray()) {
        return HeapArray::make_descriptor(typeInfo, object->array()->count_).size();
    } else {
        return HeapObject::make_descriptor(typeInfo).size();
    }
}

gc::GC::ObjectData& alloc::objectDataForObject(ObjHeader* object) noexcept {
    return HeapObjHeader::from(object).objectData();
}

ObjHeader* alloc::objectForObjectData(gc::GC::ObjectData& objectData) noexcept {
    return HeapObjHeader::from(objectData).object();
}
