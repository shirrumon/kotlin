/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "AllocatorImpl.hpp"

using namespace kotlin;

size_t alloc::allocatedHeapSize(ObjHeader* object) noexcept {
    return gc::ObjectFactory::GetAllocatedHeapSize(object);
}

gc::GC::ObjectData& objectDataForObject(ObjHeader* object) noexcept {
    return gc::ObjectFactory::NodeRef::From(object).ObjectData();
}

ObjHeader* objectForObjectData(gc::GC::ObjectData& objectData) noexcept {
    return gc::ObjectFactory::NodeRef::From(objectData)->GetObjHeader();
}
