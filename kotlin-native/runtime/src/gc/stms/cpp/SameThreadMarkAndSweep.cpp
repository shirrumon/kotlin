/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "SameThreadMarkAndSweep.hpp"

#include <cinttypes>

#include "CompilerConstants.hpp"
#include "GlobalData.hpp"
#include "GCImpl.hpp"
#include "GCStatistics.hpp"
#include "Logging.hpp"
#include "MarkAndSweepUtils.hpp"
#include "Memory.h"
#include "RootSet.hpp"
#include "Runtime.h"
#include "ThreadData.hpp"
#include "ThreadRegistry.hpp"
#include "ThreadSuspension.hpp"

using namespace kotlin;

gc::SameThreadMarkAndSweep::SameThreadMarkAndSweep(alloc::Allocator& allocator, gcScheduler::GCScheduler& gcScheduler) noexcept :

    allocator_(allocator), gcScheduler_(gcScheduler) {
    gcThread_ = ScopedThread(ScopedThread::attributes().name("GC thread"), [this] {
        while (true) {
            auto epoch = state_.waitScheduled();
            if (epoch.has_value()) {
                PerformFullGC(*epoch);
            } else {
                break;
            }
        }
    });
    RuntimeLogDebug({kTagGC}, "Same thread Mark & Sweep GC initialized");
}

gc::SameThreadMarkAndSweep::~SameThreadMarkAndSweep() {
    state_.shutdown();
}

void gc::SameThreadMarkAndSweep::PerformFullGC(int64_t epoch) noexcept {
    auto gcHandle = GCHandle::create(epoch);

    stopTheWorld(gcHandle);

    auto& scheduler = gcScheduler_;
    scheduler.onGCStart();

    state_.start(epoch);

    gc::collectRootSet<internal::MarkTraits>(gcHandle, markQueue_, [](mm::ThreadData&) { return true; });

    gc::Mark<internal::MarkTraits>(gcHandle, markQueue_);

    gc::processWeaks<DefaultProcessWeaksTraits>(gcHandle, mm::SpecialRefRegistry::instance());

    // This should really be done by each individual thread while waiting
    for (auto& thread : kotlin::mm::ThreadRegistry::Instance().LockForIter()) {
        thread.allocator().prepareForGC();
    }

    // Taking the locks before the pause is completed. So that any destroying thread
    // would not publish into the global state at an unexpected time.
    auto markedHeap = allocator_.prepareForGC(epoch);

    auto pendingFinalizers = std::move(markedHeap).sweep();

    scheduler.onGCFinish(epoch, alloc::allocatedBytes());

    resumeTheWorld(gcHandle);

    state_.finish(epoch);
    gcHandle.finalizersScheduled(pendingFinalizers.finalizersCount());
    gcHandle.finished();

    // This may start a new thread. On some pthreads implementations, this may block waiting for concurrent thread
    // destructors running. So, it must ensured that no locks are held by this point.
    // TODO: Consider having an always on sleeping finalizer thread.
    std::move(pendingFinalizers).dispatch();
}
