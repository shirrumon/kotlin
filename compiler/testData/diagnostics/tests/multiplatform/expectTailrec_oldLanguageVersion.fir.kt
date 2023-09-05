// LANGUAGE: -ProhibitExpectActualTailrecOrExternal
// MODULE: m1-common
// FILE: common.kt

expect <!NO_TAIL_CALLS_FOUND!>tailrec<!> fun foo(p: Int): Int
expect fun bar(p: Int): Int

expect <!WRONG_MODIFIER_TARGET!>tailrec<!> val notReport: String

expect class A {
    <!NO_TAIL_CALLS_FOUND!>tailrec<!> fun foo(p: Int): Int
    fun bar(p: Int): Int
}

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt
actual tailrec fun foo(p: Int): Int = foo(p)
actual tailrec fun bar(p: Int): Int = bar(p)

actual val notReport: String = "123"

actual class A {
    actual tailrec fun foo(p: Int): Int = foo(p)
    actual tailrec fun bar(p: Int): Int = bar(p)
}
