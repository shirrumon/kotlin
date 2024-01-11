// MODULE: m1-common
// FILE: common.kt

fun foo() {}
class Foo

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt

fun <!ACTUAL_MISSING!>foo<!>() {}
class <!ACTUAL_MISSING!>Foo<!>
