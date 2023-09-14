/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
@file:OptIn(kotlin.experimental.ExperimentalNativeApi::class, kotlin.native.runtime.NativeRuntimeApi::class)

import kotlin.concurrent.AtomicInt
import kotlin.concurrent.Volatile
import kotlin.native.concurrent.*
import kotlin.native.identityHashCode
import kotlin.native.internal.MemoryUsageInfo
import kotlin.native.ref.createCleaner
import kotlin.random.Random

// Copying what's done in kotlinx.benchmark
// TODO: Could we benefit, if this was in stdlib, and the compiler just new about it?
object Blackhole {
    @Volatile
    var i0: Int = Random.nextInt()
    var i1 = i0 + 1

    fun consume(value: Any?) {
        consume(value.identityHashCode())
    }

    fun consume(i: Int) {
        if ((i0 == i) && (i1 == i)) {
            i0 = i
        }
    }
}

class SmallObject {
    val data = LongArray(4) // Equivalent of 5 pointers (extra 1 is from array length)
    init {
        Blackhole.consume(data)
    }
}

class SmallObjectWithFinalizer {
    val impl = SmallObject()
    val cleaner = createCleaner(impl) {
        Blackhole.consume(it)
    }
}

class BigObject {
    val data = ByteArray(1_000_000) // ~1MiB
    init {
        // Write into every OS page.
        for (i in 0 until data.size step 4096) {
            data[i] = 42
        }
        Blackhole.consume(data)
    }
}

class BigObjectWithFinalizer {
    val impl = BigObject()
    val cleaner = createCleaner(impl) {
        Blackhole.consume(it)
    }
}

fun allocateGarbage() {
    // Total amount of objects here:
    // - 1 big object with finalizer
    // - 9 big objects
    // - 9990 small objects with finalizers
    // - 90000 small objects without finalizers
    // And total size is ~15MiB
    for (i in 0..100_000) {
        val obj = when {
            i == 50_000 -> BigObjectWithFinalizer()
            i % 10_000 == 0 -> BigObject()
            i % 10 == 0 -> SmallObjectWithFinalizer()
            else -> SmallObject()
        }
        Blackhole.consume(obj)
    }
}

class PeakRSSChecker(private val rssDiffLimitBytes: Long) {
    // On Linux, the child process might immediately commit the same amount of memory as the parent.
    // So, measure difference between peak RSS measurements.
    private val initialBytes = MemoryUsageInfo.peakResidentSetSizeBytes.also {
        check(it != 0L) { "Error trying to obtain peak RSS. Check if current platform is supported" }
    }

    fun check(): Long {
        val diffBytes = MemoryUsageInfo.peakResidentSetSizeBytes - initialBytes
        check(diffBytes <= rssDiffLimitBytes) { "Increased peak RSS by $diffBytes bytes which is more than $rssDiffLimitBytes" }
        return diffBytes
    }
}

fun main() {
    // allocateGarbage allocates ~15MiB. Make total amount per mutator ~150GiB.
    val count = 10_000
    // Total amount overall is ~600GiB
    val threadCount = 4
    val progressReportsCount = 10
    // Setting the initial boundary to ~10MiB. The scheduler will adapt this value
    // dynamically with no upper limit.
    kotlin.native.runtime.GC.targetHeapBytes = 10_000_000
    kotlin.native.runtime.GC.minHeapBytes = 10_000_000
    // Limit memory usage at ~30MiB. 3 times the initial boundary yet still
    // way less than total expected allocated amount.
    val peakRSSChecker = PeakRSSChecker(30_000_000L)

    val workers = Array(threadCount) { Worker.start() }
    val globalCount = AtomicInt(0)
    val finalGlobalCount = count * workers.size
    workers.forEach {
        it.executeAfter(0L) {
            for (i in 0 until count) {
                allocateGarbage()
                peakRSSChecker.check()
                globalCount.getAndAdd(1)
            }
        }
    }

    val reportStep = finalGlobalCount / progressReportsCount
    var lastReportCount = -reportStep
    while (true) {
        val diffPeakRss = peakRSSChecker.check()
        val currentCount = globalCount.value
        if (currentCount >= finalGlobalCount) {
            break
        }
        if (lastReportCount + reportStep <= currentCount) {
            println("Allocating iteration $currentCount of $finalGlobalCount with peak RSS increase: $diffPeakRss bytes")
            lastReportCount = currentCount
        }
    }

    workers.forEach {
        it.requestTermination().result
    }
    peakRSSChecker.check()
}
