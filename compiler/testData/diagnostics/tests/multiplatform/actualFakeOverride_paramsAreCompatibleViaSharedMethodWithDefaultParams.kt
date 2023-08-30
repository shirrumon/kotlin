// MUTE_EXPECT_ACTUAL_CLASSES_WARNING
// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt
interface Shared {
    fun sharedMethod(withDefaultParam: Int = 2) {}
}

expect class Foo : Shared

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual class Foo : Shared
