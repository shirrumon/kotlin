// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

interface I {
    fun foo(param: Int) {}
}

expect class Foo : I

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual class Foo : I {
    override fun <!ACTUAL_MISSING!>foo<!>(param: Int) {}
}
