// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

expect interface Bar
expect interface Foo : Bar

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual interface Bar {
    fun foo()
}

actual interface Foo : Bar {
    override fun foo()
}
