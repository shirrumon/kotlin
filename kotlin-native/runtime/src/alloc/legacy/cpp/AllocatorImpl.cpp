/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

using namespace kotlin;

alloc::Allocator::ThreadData::ThreadData(Allocator& allocator) noexcept
    : impl_(std_support::make_unique<Impl>(allocator.impl())) {}

alloc::Allocator::ThreadData::~ThreadData() = default;

void alloc::Allocator::ThreadData::publish() noexcept {
    impl().extraObjectDataFactoryThreadQueue().Publish();
    impl().objectFactoryThreadQueue().Publish();
}

void alloc::Allocator::ThreadData::clearForTests() noexcept {
    impl().extraObjectDataFactoryThreadQueue().ClearForTests();
    impl().objectFactoryThreadQueue().ClearForTests();
}

ALWAYS_INLINE ObjHeader* alloc::Allocator::ThreadData::allocateObject(const TypeInfo* typeInfo) noexcept {
    return impl().objectFactoryThreadQueue().CreateObject(typeInfo);
}

ALWAYS_INLINE ArrayHeader* alloc::Allocator::ThreadData::allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
    return impl().objectFactoryThreadQueue().CreateArray(typeInfo, elements);
}

ALWAYS_INLINE mm::ExtraObjectData& alloc::Allocator::ThreadData::allocateExtraObject(ObjHeader* object, const TypeInfo* typeInfo) noexcept {
    return impl().extraObjectDataFactoryThreadQueue().CreateExtraObjectDataForObject(object, typeInfo);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.Uninstall();
    impl().extraObjectDataFactoryThreadQueue().DestroyExtraObjectData(extraObject);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    impl().extraObjectDataFactoryThreadQueue().DestroyExtraObjectData(extraObject);
}

alloc::Allocator::Allocator() noexcept
    : impl_(std_support::make_unique<Impl>()) {}

alloc::Allocator::~Allocator() = default;

void alloc::Allocator::clearForTests() noexcept {
    stopFinalizerThreadIfRunning();
    impl().extraObjectDataFactory().ClearForTests();
    impl().objectFactory().ClearForTests();
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

size_t alloc::allocatedHeapSize(ObjHeader* object) noexcept {
    return gc::ObjectFactory::GetAllocatedHeapSize(object);
}

gc::GC::ObjectData& alloc::objectDataForObject(ObjHeader* object) noexcept {
    return gc::ObjectFactory::NodeRef::From(object).ObjectData();
}

ObjHeader* alloc::objectForObjectData(gc::GC::ObjectData& objectData) noexcept {
    return gc::ObjectFactory::NodeRef::From(objectData)->GetObjHeader();
}
