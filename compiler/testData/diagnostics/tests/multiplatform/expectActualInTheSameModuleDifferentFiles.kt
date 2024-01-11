// MODULE: m1-common
// FILE: common.kt

expect class A

// FILE: common2.kt
actual class A

// MODULE: m1-jvm()()(m1-common)
// FILE: jvm.kt

expect class B

// FILE: jvm2.kt
actual class B
