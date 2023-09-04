/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.collections

import kotlin.test.*

// TODO: Check which parts are already tested in libraries/stdlib/test/collections
class RangeTest {
    @Test fun rangeInt() {
        val result = buildList {
            for (i in 1..3)
                add(i)
        }
        assertContentEquals(listOf(1, 2, 3), result)
    }

    @Test fun rangeChar() {
        val result = buildList {
            for (i in 'a'..'d')
                add(i)
        }
        assertContentEquals(listOf('a', 'b', 'c', 'd'), result)
    }
}