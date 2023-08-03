/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "GC.hpp"
#include "Memory.h"

namespace kotlin::alloc {

void initObjectPool() noexcept;
void compactObjectPoolInCurrentThread() noexcept;
size_t allocatedBytes() noexcept;
size_t allocatedHeapSize(ObjHeader* object) noexcept;
gc::GC::ObjectData& objectDataForObject(ObjHeader* object) noexcept;
ObjHeader* objectForObjectData(gc::GC::ObjectData& objectData) noexcept;

}
