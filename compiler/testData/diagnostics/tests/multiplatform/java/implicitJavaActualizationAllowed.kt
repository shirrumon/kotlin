// WITH_STDLIB

// MODULE: m1-common
// FILE: common.kt

import kotlin.jvm.ImplicitlyActualizedByNonKotlin

@OptIn(ImplicitlyActualizedByNonKotlin::class)
expect class Foo() {
    fun foo()
}

// MODULE: m2-jvm()()(m1-common)
// FILE: Foo.java

public class Foo {
    public void foo() {
    }
}
