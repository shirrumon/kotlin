/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

#include "ThreadData.hpp"

using namespace kotlin;

alloc::SweepPipeline::SweepPipeline(Allocator::Impl& allocator, uint64_t epoch) noexcept :
    epoch_(epoch),
    extraObjectFactoryIterable_(allocator.extraObjectDataFactory().LockForIter()),
    objectFactoryIterable_(allocator.objectFactory().LockForIter()) {}

size_t alloc::internal::sweep(Allocator::Impl& impl, uint64_t epoch) noexcept {
    RuntimeAssert(impl.sweepPipeline().has_value(), "Sweep must be going on for epoch %" PRIu64, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->epoch_ == epoch, "Sweep is going for epoch %" PRIu64 " but we're expecting epoch %" PRIu64,
            impl.sweepPipeline()->epoch_, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->objectFactoryIterable_.has_value(), "Sweep is going for epoch %" PRIu64 " but the heap is not locked",
            epoch);
    RuntimeAssert(
            impl.sweepPipeline()->extraObjectFactoryIterable_.has_value(),
            "Sweep is going for epoch %" PRIu64 " but the extra objects heap is not locked", epoch);
    RuntimeAssert(
            !impl.sweepPipeline()->finalizerQueue_.has_value(), "Sweep is going for epoch %" PRIu64 " but it already has finalizer queue",
            epoch);

    auto gcHandle = gc::GCHandle::getByEpoch(epoch);
    SweepExtraObjects<DefaultSweepTraits<ObjectFactoryImpl>>(gcHandle, *impl.sweepPipeline()->extraObjectFactoryIterable_);
    impl.sweepPipeline()->extraObjectFactoryIterable_ = std::nullopt;
    auto finalizerQueue = Sweep<DefaultSweepTraits<ObjectFactoryImpl>>(gcHandle, *impl.sweepPipeline()->objectFactoryIterable_);
    impl.sweepPipeline()->objectFactoryIterable_ = std::nullopt;
    compactObjectPoolInMainThread();
    auto finalizersCount = finalizerQueue.size();
    impl.sweepPipeline()->finalizerQueue_ = std::move(finalizerQueue);
    return finalizersCount;
}

void alloc::internal::pendingFinalizersDispatch(Allocator::Impl& impl, uint64_t epoch) noexcept {
    RuntimeAssert(impl.sweepPipeline().has_value(), "Sweep must be going on for epoch %" PRIu64, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->epoch_ == epoch, "Sweep is going for epoch %" PRIu64 " but we're expecting epoch %" PRIu64,
            impl.sweepPipeline()->epoch_, epoch);
    RuntimeAssert(
            !impl.sweepPipeline()->objectFactoryIterable_.has_value(),
            "Sweep is going for epoch %" PRIu64 " but the heap was not processed", epoch);
    RuntimeAssert(
            !impl.sweepPipeline()->extraObjectFactoryIterable_.has_value(),
            "Sweep is going for epoch %" PRIu64 " but the extra objects heap was not processed", epoch);
    RuntimeAssert(
            impl.sweepPipeline()->finalizerQueue_.has_value(), "Sweep is going for epoch %" PRIu64 " but it does not have finalizer queue",
            epoch);

    impl.finalizerProcessor().ScheduleTasks(std::move(*impl.sweepPipeline()->finalizerQueue_), epoch);
    impl.sweepPipeline() = std::nullopt;
}

void alloc::internal::traverseObjects(Allocator::Impl& impl, uint64_t epoch, std::function<void(ObjHeader*)> f) noexcept {
    RuntimeAssert(impl.sweepPipeline().has_value(), "Sweep must be going on for epoch %" PRIu64, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->epoch_ == epoch, "Sweep is going for epoch %" PRIu64 " but we're expecting epoch %" PRIu64,
            impl.sweepPipeline()->epoch_, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->objectFactoryIterable_.has_value(), "Sweep is going for epoch %" PRIu64 " but the heap is not locked",
            epoch);

    for (auto objRef : *impl.sweepPipeline()->objectFactoryIterable_) {
        f(objRef.GetObjHeader());
    }
}

alloc::Allocator::ThreadData::ThreadData(Allocator& allocator) noexcept : impl_(std::make_unique<Impl>(allocator.impl())) {}

alloc::Allocator::ThreadData::~ThreadData() = default;

ALWAYS_INLINE ObjHeader* alloc::Allocator::ThreadData::allocateObject(const TypeInfo* typeInfo) noexcept {
    return impl_->objectFactoryThreadQueue().CreateObject(typeInfo);
}

ALWAYS_INLINE ArrayHeader* alloc::Allocator::ThreadData::allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
    return impl_->objectFactoryThreadQueue().CreateArray(typeInfo, elements);
}

ALWAYS_INLINE mm::ExtraObjectData& alloc::Allocator::ThreadData::allocateExtraObjectData(
        ObjHeader* object, const TypeInfo* typeInfo) noexcept {
    return impl_->extraObjectDataFactoryThreadQueue().CreateExtraObjectDataForObject(object, typeInfo);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    impl_->extraObjectDataFactoryThreadQueue().DestroyExtraObjectData(extraObject);
}

void alloc::Allocator::ThreadData::prepareForGC() noexcept {
    impl_->extraObjectDataFactoryThreadQueue().Publish();
    impl_->objectFactoryThreadQueue().Publish();
}

void alloc::Allocator::ThreadData::clearForTests() noexcept {
    impl_->extraObjectDataFactoryThreadQueue().ClearForTests();
    impl_->objectFactoryThreadQueue().ClearForTests();
}

alloc::Allocator::Allocator() noexcept : impl_(std::make_unique<Impl>()) {}

alloc::Allocator::~Allocator() = default;

alloc::MarkedHeap alloc::Allocator::prepareForGC(uint64_t epoch) noexcept {
    impl_->sweepPipeline().emplace(*impl_, epoch);
    return MarkedHeap(impl(), epoch);
}

void alloc::Allocator::startFinalizerThreadIfNeeded() noexcept {
    NativeOrUnregisteredThreadGuard guard(true);
    impl_->finalizerProcessor().StartFinalizerThreadIfNone();
    impl_->finalizerProcessor().WaitFinalizerThreadInitialized();
}

void alloc::Allocator::stopFinalizerThreadIfRunning() noexcept {
    NativeOrUnregisteredThreadGuard guard(true);
    impl_->finalizerProcessor().StopFinalizerThread();
}

bool alloc::Allocator::finalizersThreadIsRunning() noexcept {
    return impl_->finalizerProcessor().IsRunning();
}

void alloc::Allocator::clearForTests() noexcept {
    impl_->finalizerProcessor().StopFinalizerThread();
    impl_->extraObjectDataFactory().ClearForTests();
    impl_->objectFactory().ClearForTests();
}

gc::GC::ObjectData& alloc::objectDataForObject(ObjHeader* object) noexcept {
    return ObjectFactoryImpl::NodeRef::From(object).ObjectData();
}

ObjHeader* alloc::objectForObjectData(gc::GC::ObjectData& objectData) noexcept {
    return ObjectFactoryImpl::NodeRef::From(objectData)->GetObjHeader();
}

size_t alloc::allocatedHeapSize(ObjHeader* object) noexcept {
    return ObjectFactoryImpl::GetAllocatedHeapSize(object);
}

void alloc::destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.Uninstall();
    auto* threadData = mm::ThreadRegistry::Instance().CurrentThreadData();
    threadData->allocator().impl().extraObjectDataFactoryThreadQueue().DestroyExtraObjectData(extraObject);
}
