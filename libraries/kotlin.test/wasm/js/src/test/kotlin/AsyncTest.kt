/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import kotlin.js.Promise
import kotlin.test.*

private external fun setTimeout(body: () -> Unit, timeout: Int)

class AsyncTest {

    var log = "abc"

    var afterLog = ""

    @BeforeTest
    fun before() {
        assertEquals(log, "abc")
        log = ""
    }

    @AfterTest
    fun after() {
        // uncomment after bootstrap
        // KT-61888
        //assertEquals("after", afterLog)
    }

    fun promise(v: Int) = Promise { resolve, _ ->
        log += "a"
        setTimeout({ log += "c"; afterLog += "after"; resolve(v.toJsNumber()) }, 100)
        log += "b"
    }

    @Test
    fun checkAsyncOrder(): Promise<JsNumber> {
        assertEquals(log, "")

        log += 1

        val p1 = promise(10)

        log += 2

        val p2 = p1.then { result ->
            assertEquals(log, "1ab23c")
            result
        }

        log += 3

        return p2
    }
}