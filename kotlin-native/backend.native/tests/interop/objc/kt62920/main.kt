@file:OptIn(kotlin.ExperimentalStdlibApi::class)

import objclib.*

import kotlin.concurrent.AtomicInt
import kotlin.concurrent.AtomicIntArray
import kotlin.native.concurrent.*

class C1
class C2
class C3
class C4

// The last `i` of `Ci`.
const val MAX_STAGE = 4

val canRunStage = AtomicInt(0)
val hasRunStage = AtomicIntArray(MAX_STAGE + 1)

fun test() {
    hasRunStage.getAndIncrement(0)

    while (canRunStage.value != 1) {}
    useObject(C1())
    hasRunStage.getAndIncrement(1)

    while (canRunStage.value != 2) {}
    useObject(C2())
    hasRunStage.getAndIncrement(2)

    while (canRunStage.value != 3) {}
    useObject(C3())
    hasRunStage.getAndIncrement(3)

    while (canRunStage.value != 4) {}
    useObject(C4())
    hasRunStage.getAndIncrement(4)
}

fun main() {
    val workers = Array(10) { Worker.start() }

    workers.forEach { it.executeAfter(0, ::test) }

    while (hasRunStage[0] != workers.size) {}
    (1..MAX_STAGE).forEach { stage ->
        canRunStage.value = stage
        while (hasRunStage[stage] != workers.size) {}
    }

    workers.forEach { it.requestTermination().result }
}