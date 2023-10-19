// LANGUAGE: +MultiPlatformProjects
// IGNORE_BACKEND_K1: ANY
// IGNORE_BACKEND_K2: JVM_IR
// ISSUE: KT-62145, KT-62713

// MODULE: lib
// FILE: lib.kt
fun foo(): String {
    return "lib func "
}

val bar: String
    get() = "lib prop"

// MODULE: common(lib)
// FILE: common.kt
fun commonBox(): String {
    return foo() + bar
}

// MODULE: platform(lib)()(common)
// FILE: platform.kt
fun foo(): String {
    return "O"
}

val bar: String
    get() = "K"

fun box(): String {
    return commonBox()
}
