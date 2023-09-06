// MODULE: m1-common
// FILE: common.kt

open class Base {
    open val foo: String = ""
    <!INCOMPATIBLE_MATCHING{JVM}, INCOMPATIBLE_MATCHING{JVM}!>open fun foo(): Any = ""<!>
}

<!INCOMPATIBLE_MATCHING{JVM}!>expect open class Foo : Base {
}<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual open class Foo : Base() {
    // Return type mismatch isn't reported in K2 because K2 doesn't compare return types on frontend.
    // It reports INCOMPATIBLE_MATCHING on backend instead KT-60961.
    override fun foo(): String = ""
}
