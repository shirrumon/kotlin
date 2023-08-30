// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

expect class Clazz {
    class Nested
}

expect interface Interface

expect object Object

expect annotation class Annotation

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual class Clazz {
    actual class Nested
}

actual interface Interface

actual object Object

actual annotation class Annotation
