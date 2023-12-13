
// FILE: test.kt

fun box() {
    val x =
            42
}

// EXPECTATIONS JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:6 box
// test.kt:5 box
// test.kt:7 box

// EXPECTATIONS JS_IR
// test.kt:6 box
// test.kt:7 box

// EXPECTATIONS WASM
// test.kt:1 $box
// test.kt:6 $box
// test.kt:5 $box
// test.kt:7 $box
