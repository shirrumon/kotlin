/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.text

import kotlin.test.*

// Native-specific part of stdlib/test/text/StringBuilderTest.kt
class StringBuilderNativeTest {
    @Test fun insertString() {
        StringBuilder("my insert string test").let { sb ->
            assertFailsWith<IndexOutOfBoundsException> { sb.insert(-1, "_") }
            assertFailsWith<IndexOutOfBoundsException> { sb.insert(sb.length + 1, "_") }
        }
    }

    @Test fun insertNull() {
        StringBuilder().let { sb ->
            sb.insert(0, null as CharSequence?)
            assertEquals("null", sb.toString())
            sb.insert(2, null as Any?)
            assertEquals("nunullll", sb.toString())
            sb.insert(sb.length, null as String?)
            assertEquals("nunullllnull", sb.toString())
        }
    }
    
    @Test fun insertByte() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.toByte())
            assertEquals("42", sb.toString())
            sb.insert(1, -1.toByte())
            assertEquals("4-12", sb.toString())
            sb.insert(sb.length, 0.toByte())
            assertEquals("4-120", sb.toString())
        }
    }
    
    @Test fun insertShort() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.toShort())
            assertEquals("42", sb.toString())
            sb.insert(1, -1.toShort())
            assertEquals("4-12", sb.toString())
            sb.insert(sb.length, 0.toShort())
            assertEquals("4-120", sb.toString())
        }
    }
    
    @Test fun insertInt() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.toInt())
            assertEquals("42", sb.toString())
            sb.insert(1, -1.toInt())
            assertEquals("4-12", sb.toString())
            sb.insert(sb.length, 0.toInt())
            assertEquals("4-120", sb.toString())
        }
    }

    @Test fun insertLong() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.toLong())
            assertEquals("42", sb.toString())
            sb.insert(1, -1.toLong())
            assertEquals("4-12", sb.toString())
            sb.insert(sb.length, 0.toLong())
            assertEquals("4-120", sb.toString())
        }
    }
    
    @Test fun insertFloat() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.3f)
            assertEquals("42.3", sb.toString())
            sb.insert(1, -1.5f)
            assertEquals("4-1.52.3", sb.toString())
            sb.insert(sb.length, 0.0f)
            assertEquals("4-1.52.30.0", sb.toString())
        }
    }

    @Test fun insertDouble() {
        StringBuilder().let { sb ->
            sb.insert(0, 42.3)
            assertEquals("42.3", sb.toString())
            sb.insert(1, -1.5)
            assertEquals("4-1.52.3", sb.toString())
            sb.insert(sb.length, 0.0)
            assertEquals("4-1.52.30.0", sb.toString())
        }
    }

    @Test fun insertRange() {
        StringBuilder().let { sb ->
            sb.insertRange(0, "1234", 0, 0)
            assertEquals("", sb.toString())
            sb.insertRange(0, "1234", 0, 1)
            assertEquals("1", sb.toString())
            sb.insertRange(0, "1234", 1, 3)
            assertEquals("231", sb.toString())

            sb.insertRange(2, "1234", 0, 0)
            assertEquals("231", sb.toString())
            sb.insertRange(2, "1234", 0, 1)
            assertEquals("2311", sb.toString())
            sb.insertRange(2, "1234", 1, 3)
            assertEquals("232311", sb.toString())

            sb.insertRange(sb.length, "1234", 0, 0)
            assertEquals("232311", sb.toString())
            sb.insertRange(sb.length, "1234", 0, 1)
            assertEquals("2323111", sb.toString())
            sb.insertRange(sb.length, "1234", 1, 3)
            assertEquals("232311123", sb.toString())

            assertFails { sb.insertRange(-1, "1234", 0, 0) }
            assertFails { sb.insertRange(sb.length + 1, "1234", 0, 0) }
            assertFails { sb.insertRange(0, "1234", -1, 0) }
            assertFails { sb.insertRange(0, "1234", 0, 5) }
            assertFails { sb.insertRange(0, "1234", 2, 0) }
        }
    }
}