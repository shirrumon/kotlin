/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include "GC.hpp"
#include "Memory.h"
#include "Utils.hpp"
#include "std_support/Memory.hpp"

namespace kotlin::alloc {

class Allocator : private Pinned {
public:
    class Impl;

    class ThreadData : private Pinned {
    public:
        class Impl;

        ThreadData(Allocator& allocator) noexcept;
        ~ThreadData();

        Impl& impl() noexcept { return *impl_; }

        void publish() noexcept;
        void clearForTests() noexcept;

        ObjHeader* allocateObject(const TypeInfo* typeInfo) noexcept;
        ArrayHeader* allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept;
        mm::ExtraObjectData& allocateExtraObject(ObjHeader* object, const TypeInfo* typeInfo) noexcept;
        void destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept;
        void destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept;

    private:
        std_support::unique_ptr<Impl> impl_;
    };

    Allocator() noexcept;
    ~Allocator();

    Impl& impl() noexcept { return *impl_; }

    void clearForTests() noexcept;

    void startFinalizerThreadIfNeeded() noexcept;
    void stopFinalizerThreadIfRunning() noexcept;
    bool finalizersThreadIsRunning() noexcept;

private:
    std_support::unique_ptr<Impl> impl_;
};

void initObjectPool() noexcept;
void compactObjectPoolInCurrentThread() noexcept;
size_t allocatedBytes() noexcept;
size_t allocatedHeapSize(ObjHeader* object) noexcept;
gc::GC::ObjectData& objectDataForObject(ObjHeader* object) noexcept;
ObjHeader* objectForObjectData(gc::GC::ObjectData& objectData) noexcept;

}
