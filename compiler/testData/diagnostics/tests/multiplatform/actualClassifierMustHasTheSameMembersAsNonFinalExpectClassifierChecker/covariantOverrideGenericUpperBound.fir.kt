// MODULE: m1-common
// FILE: common.kt

interface I

open class Base {
    <!INCOMPATIBLE_MATCHING{JVM}!>open fun foo(): I = null!!<!>
}

<!INCOMPATIBLE_MATCHING{JVM}!>expect open class Foo<T : I> : Base {
}<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual open class Foo<T : I> : Base() {
    // Return type mismatch isn't reported in K2 because K2 doesn't compare return types on frontend.
    // It reports INCOMPATIBLE_MATCHING on backend instead KT-60961.
    override fun foo(): T = null!!
}
