/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "GCImpl.hpp"

#include "Common.h"
#include "GC.hpp"
#include "GCStatistics.hpp"
#include "Logging.hpp"
#include "ObjectOps.hpp"
#include "ThreadData.hpp"
#include "std_support/Memory.hpp"

using namespace kotlin;

gc::GC::ThreadData::ThreadData(GC& gc, mm::ThreadData& threadData) noexcept : impl_(std_support::make_unique<Impl>(gc, threadData)) {}

gc::GC::ThreadData::~ThreadData() = default;

void gc::GC::ThreadData::Publish() noexcept {
    impl_->allocator().publish();
}

void gc::GC::ThreadData::ClearForTests() noexcept {
    impl_->allocator().clearForTests();
}

ALWAYS_INLINE ObjHeader* gc::GC::ThreadData::CreateObject(const TypeInfo* typeInfo) noexcept {
    return impl_->allocator().allocateObject(typeInfo);
}

ALWAYS_INLINE ArrayHeader* gc::GC::ThreadData::CreateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept {
    return impl_->allocator().allocateArray(typeInfo, elements);
}

ALWAYS_INLINE mm::ExtraObjectData& gc::GC::ThreadData::CreateExtraObjectDataForObject(
        ObjHeader* object, const TypeInfo* typeInfo) noexcept {
    return impl_->allocator().allocateExtraObject(object, typeInfo);
}

ALWAYS_INLINE void gc::GC::ThreadData::DestroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    impl_->allocator().destroyUnattachedExtraObjectData(extraObject);
}

void gc::GC::ThreadData::OnSuspendForGC() noexcept { }

void gc::GC::ThreadData::safePoint() noexcept {}

gc::GC::GC(gcScheduler::GCScheduler&) noexcept : impl_(std_support::make_unique<Impl>()) {
    RuntimeLogInfo({kTagGC}, "No-op GC initialized");
}

gc::GC::~GC() = default;

void gc::GC::ClearForTests() noexcept {
    impl_->allocator().clearForTests();
    GCHandle::ClearForTests();
}

void gc::GC::StartFinalizerThreadIfNeeded() noexcept {}

void gc::GC::StopFinalizerThreadIfRunning() noexcept {}

bool gc::GC::FinalizersThreadIsRunning() noexcept {
    return false;
}

// static
ALWAYS_INLINE void gc::GC::processObjectInMark(void* state, ObjHeader* object) noexcept {}

// static
ALWAYS_INLINE void gc::GC::processArrayInMark(void* state, ArrayHeader* array) noexcept {}

// static
ALWAYS_INLINE void gc::GC::processFieldInMark(void* state, ObjHeader* field) noexcept {}

int64_t gc::GC::Schedule() noexcept {
    return 0;
}

void gc::GC::WaitFinished(int64_t epoch) noexcept {}

void gc::GC::WaitFinalizers(int64_t epoch) noexcept {}

bool gc::isMarked(ObjHeader* object) noexcept {
    RuntimeAssert(false, "Should not reach here");
    return true;
}

ALWAYS_INLINE OBJ_GETTER(gc::tryRef, std::atomic<ObjHeader*>& object) noexcept {
    RETURN_OBJ(object.load(std::memory_order_relaxed));
}

ALWAYS_INLINE bool gc::tryResetMark(GC::ObjectData& objectData) noexcept {
    RuntimeAssert(false, "Should not reach here");
    return true;
}

// static
ALWAYS_INLINE void gc::GC::DestroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept {
    auto* threadData = mm::ThreadRegistry::Instance().CurrentThreadData();
    threadData->gc().impl().allocator().destroyExtraObjectData(extraObject);
}

// static
ALWAYS_INLINE uint64_t type_layout::descriptor<gc::GC::ObjectData>::type::size() noexcept {
    return 0;
}

// static
ALWAYS_INLINE size_t type_layout::descriptor<gc::GC::ObjectData>::type::alignment() noexcept {
    return 1;
}

// static
ALWAYS_INLINE gc::GC::ObjectData* type_layout::descriptor<gc::GC::ObjectData>::type::construct(uint8_t* ptr) noexcept {
    return reinterpret_cast<gc::GC::ObjectData*>(ptr);
}
