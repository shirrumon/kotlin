// MODULE: m1-common
// FILE: common.kt

expect class A
actual class A

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt

expect class B
actual class B
