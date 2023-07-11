// MODULE: m1-common
// FILE: common.kt

expect interface Bar
interface Baz : Bar {
    // Different behaviour in K1 and K2: KT-60142
    fun foo()
}
expect interface Foo : Baz

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual interface Bar {
    fun foo()
}

actual interface Foo : Baz {
    actual override fun foo()
}
