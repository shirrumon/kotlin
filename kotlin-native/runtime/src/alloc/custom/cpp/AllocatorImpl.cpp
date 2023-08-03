/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

#include "GCApi.hpp"

using namespace kotlin;

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
