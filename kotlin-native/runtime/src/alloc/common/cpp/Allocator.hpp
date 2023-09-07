/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#pragma once

#include <cstdint>
#include <memory>

#include "GC.hpp"
#include "Utils.hpp"

namespace kotlin::alloc {

class MarkedHeap;
class PendingFinalizers;

class Allocator : private Pinned {
public:
    class Impl;

    class ThreadData : private Pinned {
    public:
        class Impl;

        explicit ThreadData(Allocator& allocator) noexcept;
        ~ThreadData();

        Impl& impl() noexcept { return *impl_; }

        ObjHeader* allocateObject(const TypeInfo* typeInfo) noexcept;
        ArrayHeader* allocateArray(const TypeInfo* typeInfo, uint32_t elements) noexcept;
        mm::ExtraObjectData& allocateExtraObjectData(ObjHeader* object, const TypeInfo* typeInfo) noexcept;
        void destroyUnattachedExtraObjectData(mm::ExtraObjectData& extraObject) noexcept;

        void prepareForGC() noexcept;

        // TODO: Move into AllocatorTestSupport.hpp
        void clearForTests() noexcept;

    private:
        std::unique_ptr<Impl> impl_;
    };

    Allocator() noexcept;
    ~Allocator();

    Impl& impl() noexcept { return *impl_; }

    [[nodiscard]] MarkedHeap prepareForGC(uint64_t epoch) noexcept;

    void startFinalizerThreadIfNeeded() noexcept;
    void stopFinalizerThreadIfRunning() noexcept;
    bool finalizersThreadIsRunning() noexcept;

    // TODO: Move into AllocatorTestSupport.hpp
    void clearForTests() noexcept;

private:
    std::unique_ptr<Impl> impl_;
};

namespace internal {

// Returns finalizer queue size.
size_t sweep(Allocator::Impl& impl, uint64_t epoch) noexcept;
void pendingFinalizersDispatch(Allocator::Impl& impl, uint64_t epoch) noexcept;
void traverseObjects(Allocator::Impl& impl, uint64_t epoch, std::function<void(ObjHeader*)> f) noexcept;

} // namespace internal

class PendingFinalizers : private MoveOnly {
public:
    PendingFinalizers(PendingFinalizers&& rhs) noexcept : allocator_(std::move(rhs.allocator_)), epoch_(rhs.epoch_) , finalizersCount_(rhs.finalizersCount_) {
        rhs.epoch_ = 0;
        rhs.finalizersCount_ = 0;
    }

    friend void swap(PendingFinalizers& lhs, PendingFinalizers& rhs) noexcept {
        using std::swap;
        swap(lhs.allocator_, rhs.allocator_);
        swap(lhs.epoch_, rhs.epoch_);
        swap(lhs.finalizersCount_, rhs.finalizersCount_);
    }

    PendingFinalizers& operator=(PendingFinalizers&& rhs) noexcept {
        PendingFinalizers tmp(std::move(rhs));
        swap(*this, tmp);
        return *this;
    }

    ~PendingFinalizers() {
        RuntimeAssert(!allocator_, "dispatch() was not called");
    }

    size_t finalizersCount() const noexcept {
        return finalizersCount_;
    }

    // Dispatch all collected finalizers. This may start finalizer thread
    // if it didn't exist before.
    void dispatch() && noexcept {
        auto tmp = std::move(*this);
        internal::pendingFinalizersDispatch(*tmp.allocator_, tmp.epoch_);
    }

private:
    friend class MarkedHeap;

    PendingFinalizers(Allocator::Impl& allocator, int64_t epoch, size_t finalizersCount) noexcept : allocator_(&allocator), epoch_(epoch), finalizersCount_(finalizersCount) {}

    raw_ptr<Allocator::Impl> allocator_;
    uint64_t epoch_;
    size_t finalizersCount_;
};

class MarkedHeap : private MoveOnly {
public:
    MarkedHeap(MarkedHeap&& rhs) noexcept : allocator_(std::move(rhs.allocator_)), epoch_(rhs.epoch_) {
        rhs.epoch_ = 0;
    }

    friend void swap(MarkedHeap& lhs, MarkedHeap& rhs) noexcept {
        using std::swap;
        swap(lhs.allocator_, rhs.allocator_);
        swap(lhs.epoch_, rhs.epoch_);
    }

    MarkedHeap& operator=(MarkedHeap&& rhs) noexcept {
        MarkedHeap tmp(std::move(rhs));
        swap(*this, tmp);
        return *this;
    }

    ~MarkedHeap() {
        RuntimeAssert(!allocator_, "sweep() was not called");
    }

    // Traverse all alive objects and run `f` on each one.
    // This only traverses objects that were visible by the
    // time `this` was constructed.
    void traverseObjects(std::function<void(ObjHeader*)> f) noexcept {
        internal::traverseObjects(*allocator_, epoch_, std::move(f));
    }

    // Sweep and return pending finalizers.
    [[nodiscard]] PendingFinalizers sweep() && noexcept {
        auto tmp = std::move(*this);
        auto finalizersCount = internal::sweep(*tmp.allocator_, tmp.epoch_);
        return PendingFinalizers(*tmp.allocator_, tmp.epoch_, finalizersCount);
    }

private:
    friend class Allocator;

    MarkedHeap(Allocator::Impl& allocator, int64_t epoch) noexcept : allocator_(&allocator), epoch_(epoch) {}

    raw_ptr<Allocator::Impl> allocator_;
    uint64_t epoch_;
};

void initObjectPool() noexcept;
// Instruct the allocator to free unused resources.
void compactObjectPoolInCurrentThread() noexcept;

gc::GC::ObjectData& objectDataForObject(ObjHeader* object) noexcept;
ObjHeader* objectForObjectData(gc::GC::ObjectData& objectData) noexcept;

// This does not take into account how much storage did the underlying allocator reserved.
size_t allocatedHeapSize(ObjHeader* object) noexcept;

size_t allocatedBytes() noexcept;

void destroyExtraObjectData(mm::ExtraObjectData& extraObject) noexcept;
}
