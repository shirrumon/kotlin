// !DIAGNOSTICS: -ACTUAL_WITHOUT_EXPECT
// LANGUAGE: -ProhibitActualTypealiasIfExpectHasDefaultParams
// MODULE: m1-common
// FILE: common.kt
expect class A {
    fun foo(p1: String = "common", p2: String = "common", p3: String)
}

expect open class WithIncompatibility {
    fun foo(p: String = "common")
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

class AImpl {
    fun foo(p1: String = "impl", p2: String = "impl", p3: String) {}
}

actual typealias <!NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS!>A<!> = AImpl

class WithIncompatibilityImpl {
    fun foo(p: String) {}
}

actual typealias WithIncompatibility = WithIncompatibilityImpl
