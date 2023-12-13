



// FILE: test.kt

fun box(): String {
    try {
        val x = "x"
        throw RuntimeException(x)
    } finally {
        return "OK"
    }
    return "FAIL"
}

// EXPECTATIONS JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:8 box:
// test.kt:9 box:
// test.kt:10 box: x:java.lang.String="x":java.lang.String
// test.kt:12 box:

// EXPECTATIONS JS_IR
// test.kt:9 box:
// test.kt:10 box: x="x":kotlin.String
// test.kt:12 box: x="x":kotlin.String
