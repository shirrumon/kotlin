/*
 * Copyright 2010-2023 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import kotlin.test.*
import kotlinx.cinterop.toKString

// Util for performing action on result of UTF-8 to UTF-16 conversion.
fun convertUtf8to16(byteArray: ByteArray, action: (String) -> Unit) {
    byteArray.decodeToString().let { action(it) }
    byteArray.toKString().let { action(it) }
}

fun main() {
    // Valid strings.
    println("Hello")
    println("Привет")
    println("\uD800\uDC00")
    println("")

    // Illegal surrogate pair -> default output
    println("\uDC00\uD800")
    // Lone surrogate -> default output
    println("\uD80012")
    println("\uDC0012")
    println("12\uD800")

    // https://github.com/JetBrains/kotlin-native/issues/1091
    val array = byteArrayOf(0xF0.toByte(), 0x9F.toByte(), 0x98.toByte(), 0xA5.toByte())
    convertUtf8to16(array) { badString ->
        assertEquals(2, badString.length)
        println(badString)
    }
}
