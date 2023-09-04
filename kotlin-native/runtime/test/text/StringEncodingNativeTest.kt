/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.text

import kotlin.test.*
import kotlinx.cinterop.toKString
import test.assertArrayContentEquals

// Native-specific part of libraries/stdlib/test/text/StringEncodingTest.kt
class StringEncodingNativeTest {
    private fun bytes(vararg elements: Int) = ByteArray(elements.size) { elements[it].toByte() }

    private fun testDecoding(isWellFormed: Boolean, expected: String, bytes: ByteArray) {
        assertEquals(expected, bytes.toKString())
        if (!isWellFormed) {
            assertFailsWith<CharacterCodingException> { bytes.toKString(throwOnInvalidSequence = true) }
        } else {
            assertEquals(expected, bytes.toKString(throwOnInvalidSequence = true))
        }
    }

    private fun testDecoding(isWellFormed: Boolean, expected: String, bytes: ByteArray, startIndex: Int, endIndex: Int) {
        assertEquals(expected, bytes.toKString(startIndex, endIndex))
        if (!isWellFormed) {
            assertFailsWith<CharacterCodingException> { bytes.toKString(startIndex, endIndex, true) }
        } else {
            assertEquals(expected, bytes.toKString(startIndex, endIndex, true))
        }
    }

    @Test fun toKString() {
        // Valid strings.
        testDecoding(true, "Hell", bytes('H'.code, 'e'.code, 'l'.code, 'l'.code, 0, 'o'.code))
        testDecoding(true, "При", bytes(-48, -97, -47, -128, -48, -72, 0, -48, -78, 0, -48, -75, -47, -126))
        testDecoding(true, "\uD800\uDC00", bytes(-16, -112, -128, -128, 0, -16, -112, -128, -128))
        testDecoding(true, "", bytes(0, 'H'.code))

        // Test manual conversion with exception throwing
        // Incorrect UTF-8 lead character -> throw.
        testDecoding(false, "\uFFFD", bytes(-1, 0, '1'.code))

        // Incomplete codepoint -> throw.
        testDecoding(false, "\uFFFD", bytes(-16, -97, -104, 0, '1'.code))
        testDecoding(false, "\uFFFD1", bytes(-16, -97, -104, '1'.code, 0, -16, -97, -104))
    }

    @Test fun toKStringSlice() {
        val array = bytes('a'.code, 'a'.code, 'a'.code, 0, 'b'.code, 'b'.code, 'b'.code, 0)

        assertFailsWith<IndexOutOfBoundsException> { array.toKString(-1, 3) }
        assertFailsWith<IndexOutOfBoundsException> { array.toKString(8, 18) }
        assertFailsWith<IndexOutOfBoundsException> { array.toKString(2, 12) }
        assertFailsWith<IndexOutOfBoundsException> { array.toKString(10, 10) }
        assertFailsWith<IllegalArgumentException> { array.toKString(3, 1) }

        testDecoding(true, "aaa", array, 0, 5)
        testDecoding(true, "a", array, 2, 4)
        testDecoding(true, "", array, 3, 5)
        testDecoding(true, "bb", array, 4, 6)
        testDecoding(true, "bbb", array, 4, 7)
        testDecoding(true, "bbb", array, 4, 8)
        testDecoding(true, "bb", array, 5, 8)
        testDecoding(true, "b", array, 6, 8)
        testDecoding(true, "", array, 7, 8)
        testDecoding(true, "", array, 8, 8)
    }
}