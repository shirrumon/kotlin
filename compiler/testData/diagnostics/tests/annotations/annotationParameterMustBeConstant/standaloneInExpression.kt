// FIR_IDENTICAL
//!DIAGNOSTICS: -UNUSED_PARAMETER

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun <T> test1(t1: T, t2: @kotlin.internal.NoInfer T): T = t1

fun usage() {
    test1(1, <!TYPE_MISMATCH("Int; String")!>"312"<!>)
}

