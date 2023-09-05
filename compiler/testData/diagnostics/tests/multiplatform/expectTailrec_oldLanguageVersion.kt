// LANGUAGE: -ProhibitExpectActualTailrecOrExternal
// MODULE: m1-common
// FILE: common.kt

expect tailrec fun foo(p: Int): Int
expect fun bar(p: Int): Int

expect <!WRONG_MODIFIER_TARGET, WRONG_MODIFIER_TARGET{JVM}!>tailrec<!> val notReport: String

expect class A {
    tailrec fun foo(p: Int): Int
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
