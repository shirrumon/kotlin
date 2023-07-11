// MODULE: m1-common
// FILE: common.kt

interface Foo {
    fun foo()
}

expect open class Bar() : Foo

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual open class Bar : Foo {
    actual <!ACTUAL_WITHOUT_EXPECT!>final<!> override fun foo() {}
}
