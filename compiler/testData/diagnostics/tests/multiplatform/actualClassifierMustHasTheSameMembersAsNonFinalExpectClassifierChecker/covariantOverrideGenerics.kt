// MODULE: m1-common
// FILE: common.kt

open class Base<R> {
    open fun foo(): R = null!!
}

expect open class Foo<R, T : R> : Base<R> {
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual open <!ACTUAL_CLASSIFIER_MUST_HAVE_THE_SAME_MEMBERS_AS_NON_FINAL_EXPECT_CLASSIFIER!>class Foo<!><E, F : E> : Base<E>() {
    // Return type mismatch isn't reported in K2 because K2 doesn't compare return types on frontend.
    // It reports INCOMPATIBLE_MATCHING on backend instead KT-60961.
    override fun foo(): <!RETURN_TYPE_CHANGED_IN_NON_FINAL_EXPECT_CLASSIFIER_ACTUALIZATION!>F<!> = null!!
}
