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

// Keep a class to ensure we allocate in heap.
// TODO: Explicitly protect it from escape analysis.
// TODO: Allocate a variety of differently sized objects instead.
class MemoryHog(val size: Int, val value: Byte, val stride: Int) {
    val data = ByteArray(size)

    init {
        for (i in 0 until size step stride) {
            data[i] = value
        }
        Blackhole.consume(data)
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
    // One item is ~10MiB.
    val size = 10_000_000
    // Total amount per mutator is ~1TiB.
    val count = 100_000
    // Total amount overall is ~4TiB
    val threadCount = 4
    val value: Byte = 42
    // Try to make sure each page is written
    val stride = 4096
    val progressReportsCount = 10
    // Testing GC scheduler with dynamic boundary. Setting the initial boundary to ~100MiB
    kotlin.native.runtime.GC.targetHeapBytes = 100_000_000
    // Limit memory usage at ~300MiB. 3 times the initial boundary yet still
    // way less than total expected allocated amount.
    val peakRSSChecker = PeakRSSChecker(300_000_000L)

    val workers = Array(threadCount) { Worker.start() }
    val globalCount = AtomicInt(0)
    val finalGlobalCount = count * workers.size
    workers.forEach {
        it.executeAfter(0L) {
            for (i in 0 until count) {
                MemoryHog(size, value, stride)
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
