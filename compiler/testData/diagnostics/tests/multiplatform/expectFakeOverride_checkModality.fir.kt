// MODULE: m1-common
// FILE: common.kt

interface Foo {
    <!INCOMPATIBLE_MATCHING{JVM}!>fun foo()<!>
}

<!INCOMPATIBLE_MATCHING{JVM}!>expect open class Bar() : Foo<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual open class Bar : Foo {
    actual final override fun <!ACTUAL_WITHOUT_EXPECT!>foo<!>() {}
}
