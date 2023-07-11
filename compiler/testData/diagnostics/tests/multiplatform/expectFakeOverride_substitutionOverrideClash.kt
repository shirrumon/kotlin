// MODULE: m1-common
// FILE: common.kt

abstract class Foo<T> {
    open fun foo(a: String) {}
    abstract fun foo(a: T)
}

expect class Bar : Foo<Baz> {
}

expect class Baz

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Baz = String

actual class Bar : Foo<String>() {
    actual override fun <!AMBIGUOUS_EXPECTS!>foo<!>(a: String) {}
}
