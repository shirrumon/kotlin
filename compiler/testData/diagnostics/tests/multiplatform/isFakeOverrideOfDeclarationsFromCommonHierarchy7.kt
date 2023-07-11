// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

expect interface Bar {
    fun foo()
}
expect interface Baz : Bar
expect interface Foo : Baz

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual interface Bar {
    actual fun foo()
}
actual interface Baz : Bar {
    actual override fun foo()
}

actual interface Foo : Baz {
    actual override fun foo()
}
