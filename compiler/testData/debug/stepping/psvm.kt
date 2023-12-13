// IGNORE_BACKEND: WASM
// ^^ Because main function will be called firstly with empty arguments that will chrash runtime
// FILE: test.kt

fun main(args: Array<String>) {
    args[0]
}

fun box() {
    main(arrayOf("OK"))
}

// EXPECTATIONS JVM_IR JVM_IR +USE_INLINE_SCOPES_NUMBERS
// test.kt:10 box
// test.kt:6 main
// test.kt:7 main
// test.kt:11 box

// EXPECTATIONS JS_IR
// test.kt:10 box
// test.kt:6 main
// test.kt:7 main
// test.kt:11 box
