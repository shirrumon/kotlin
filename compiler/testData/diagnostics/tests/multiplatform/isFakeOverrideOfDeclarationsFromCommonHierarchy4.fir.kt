// MODULE: m1-common
// FILE: common.kt

expect interface Bar
interface Baz : Bar
expect interface Foo : Baz

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual interface Bar {
    fun foo()
}

actual interface Foo : Baz {
    // K2 false positive ACTUAL_MISSING: KT-60143
    override fun <!ACTUAL_MISSING!>foo<!>()
}
