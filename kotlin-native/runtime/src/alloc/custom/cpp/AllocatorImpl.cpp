/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

#include "GCApi.hpp"
#include "ThreadData.hpp"

using namespace kotlin;

alloc::SweepPipeline::SweepPipeline(Allocator::Impl& allocator, uint64_t epoch) noexcept : epoch_(epoch) {}

size_t alloc::internal::sweep(Allocator::Impl& impl, uint64_t epoch) noexcept {
    RuntimeAssert(impl.sweepPipeline().has_value(), "Sweep must be going on for epoch %" PRIu64, epoch);
    RuntimeAssert(
            impl.sweepPipeline()->epoch_ == epoch, "Sweep is going for epoch %" PRIu64 " but we're expecting epoch %" PRIu64,
            impl.sweepPipeline()->epoch_, epoch);
    RuntimeAssert(
            !impl.sweepPipeline()->finalizerQueue_.has_value(), "Sweep is going for epoch %" PRIu64 " but it already has finalizer queue",
            epoch);

    auto gcHandle = gc::GCHandle::getByEpoch(epoch);
    // also sweeps extraObjects
    auto finalizerQueue = impl.heap().Sweep(gcHandle);
    for (auto& thread : kotlin::mm::ThreadRegistry::Instance().LockForIter()) {
        finalizerQueue.TransferAllFrom(thread.allocator().impl().alloc().ExtractFinalizerQueue());
    }
    finalizerQueue.TransferAllFrom(impl.heap().ExtractFinalizerQueue());
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

    impl.heap().TraverseObjects(std::move(f));
}

alloc::Allocator::ThreadData::ThreadData(Allocator& allocator) noexcept : impl_(std::make_unique<Impl>(allocator.impl())) {}

alloc::Allocator::ThreadData::~ThreadData() = default;

ALWAYS_INLINE ObjHeader* alloc::Allocator::ThreadData::allocateObject(const TypeInfo* typeInfo) noexcept {
    return impl_->alloc().CreateObject(typeInfo);
}

ALWAYS_INLINE ArrayHeader* alloc::Allocator::ThreadData::allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
    return impl_->alloc().CreateArray(typeInfo, elements);
}

ALWAYS_INLINE mm::ExtraObjectData& alloc::Allocator::ThreadData::allocateExtraObjectData(
        ObjHeader* object, const TypeInfo* typeInfo) noexcept {
    return impl_->alloc().CreateExtraObjectDataForObject(object, typeInfo);
}

ALWAYS_INLINE void alloc::Allocator::ThreadData::destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.setFlag(mm::ExtraObjectData::FLAGS_SWEEPABLE);
}

void alloc::Allocator::ThreadData::prepareForGC() noexcept {
    impl_->alloc().PrepareForGC();
}

void alloc::Allocator::ThreadData::clearForTests() noexcept {
    impl_->alloc().PrepareForGC();
}

alloc::Allocator::Allocator() noexcept : impl_(std::make_unique<Impl>()) {}

alloc::Allocator::~Allocator() = default;

void alloc::Allocator::prepareForGC() noexcept {
    impl_->heap().PrepareForGC();
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
    impl_->heap().ClearForTests();
}

void alloc::initObjectPool() noexcept {}

void alloc::compactObjectPoolInCurrentThread() noexcept {}

gc::GC::ObjectData& alloc::objectDataForObject(ObjHeader* object) noexcept {
    return HeapObjHeader::from(object).objectData();
}

ObjHeader* alloc::objectForObjectData(gc::GC::ObjectData& objectData) noexcept {
    return HeapObjHeader::from(objectData).object();
}

size_t alloc::allocatedHeapSize(ObjHeader* object) noexcept {
    return CustomAllocator::GetAllocatedHeapSize(object);
}

size_t alloc::allocatedBytes() noexcept {
    return GetAllocatedBytes();
}

void alloc::destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    extraObject.ReleaseAssociatedObject();
    extraObject.setFlag(mm::ExtraObjectData::FLAGS_FINALIZED);
}
