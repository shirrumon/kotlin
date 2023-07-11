// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

expect interface Bar {
    fun foo()
}
expect interface Foo : Bar

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual interface Bar {
    actual fun foo()
}

actual interface Foo : Bar {
    actual override fun foo()
}
