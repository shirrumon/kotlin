/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#ifndef RUNTIME_MM_OBJECT_OPS_H
#define RUNTIME_MM_OBJECT_OPS_H

#include "Memory.h"

namespace kotlin {
namespace mm {

class ThreadData;

// TODO: Make sure these operations work with any kind of thread stopping: safepoints and signals.

// TODO: Consider adding some kind of an `Object` type (that wraps `ObjHeader*`) which
//       will have these operations for a friendlier API.

// TODO: `OBJ_GETTER` is used because the returned objects needs to be accessible via the rootset before the function
//       returns. If we had a different way to efficiently keep the object in the roots, `OBJ_GETTER` can be removed.

void SetStackRef(ObjHeader** location, ObjHeader* value) noexcept;
void SetHeapRef(ObjHeader** location, ObjHeader* value) noexcept;
void SetHeapRefAtomic(ObjHeader** location, ObjHeader* value) noexcept;
void SetHeapRefAtomicSeqCst(ObjHeader** location, ObjHeader* value) noexcept;
OBJ_GETTER(ReadHeapRefAtomic, ObjHeader** location) noexcept;
OBJ_GETTER(CompareAndSwapHeapRef, ObjHeader** location, ObjHeader* expected, ObjHeader* value) noexcept;
bool CompareAndSetHeapRef(ObjHeader** location, ObjHeader* expected, ObjHeader* value) noexcept;
OBJ_GETTER(GetAndSetHeapRef, ObjHeader** location, ObjHeader* value) noexcept;
OBJ_GETTER(AllocateObject, ThreadData* threadData, const TypeInfo* typeInfo) noexcept;
OBJ_GETTER(AllocateArray, ThreadData* threadData, const TypeInfo* typeInfo, uint32_t elements) noexcept;

OBJ_GETTER(GetHeapRefSeqCst, ObjHeader** location) noexcept;
int GetIntFieldSeqCst(int* location) noexcept;
void SetIntFieldSeqCst(int* location, int newValue) noexcept;
int GetAndSetIntField(int* location, int newValue) noexcept;
int FetchAndAddIntField(int* location, int delta) noexcept;
int CompareAndExchangeIntField(int* location, int* expectValue, int newValue) noexcept;
bool CompareAndSetIntField(int* location, int *expectedValue, int newValue) noexcept;

long long GetLongFieldSeqCst(long long* location) noexcept;
void SetLongFieldSeqCst(long long* location, long long newValue) noexcept;
long long GetAndSetLongField(long long* location, long long newValue) noexcept;
long long FetchAndAddLongField(long long* location, long long delta) noexcept;
long long CompareAndExchangeLongField(long long* location, long long* expectValue, long long newValue) noexcept;
bool CompareAndSetLongField(long long* location, long long *expectedValue, long long newValue) noexcept;

// This does not take into account how much storage did the underlying allocator (malloc/mimalloc) reserved.
size_t GetAllocatedHeapSize(ObjHeader* object) noexcept;

} // namespace mm
} // namespace kotlin

#endif // RUNTIME_MM_OBJECT_OPS_H
