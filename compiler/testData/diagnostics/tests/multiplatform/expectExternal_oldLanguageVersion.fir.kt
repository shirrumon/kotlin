// LANGUAGE: -ProhibitExpectActualTailrecOrExternal
// MODULE: m1-common
// FILE: common.kt

expect external fun foo()
expect fun bar()

expect <!WRONG_MODIFIER_TARGET!>external<!> var prop: String

expect var getAndSet: String
    external get
    external set

<!WRONG_MODIFIER_TARGET!>external<!> expect val explicitGetter: String
    external get

expect <!WRONG_MODIFIER_TARGET!>external<!> class A {
    external fun foo()
    fun bar()
}

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt
actual external fun foo()
actual external fun bar()

actual <!WRONG_MODIFIER_TARGET!>external<!> var prop: String

actual var getAndSet: String
    external get
    external set

actual <!WRONG_MODIFIER_TARGET!>external<!> val explicitGetter: String
    external get

actual class A {
    actual external fun foo()
    actual external fun bar()
}
