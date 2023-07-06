// WITH_STDLIB

// MODULE: m1-common
// FILE: common.kt

import kotlin.jvm.ImplicitlyActualizedByNonKotlin

<!NO_ACTUAL_FOR_EXPECT{JVM}!>@ImplicitlyActualizedByNonKotlin
expect class <!PACKAGE_OR_CLASSIFIER_REDECLARATION!>Foo<!>() {
    fun foo()
}<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: Foo.java

public class Foo {
    public void foo() {
    }
}
