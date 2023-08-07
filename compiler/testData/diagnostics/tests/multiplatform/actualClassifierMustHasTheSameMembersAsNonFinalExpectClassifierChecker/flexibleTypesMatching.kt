// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt

expect open class Base {
    open fun foo(): MutableList<String>
}

expect open class Foo : Base {

}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual typealias Base = BaseJava

actual open class Foo : Base() {
    // K1 and K2 see the world differently (K1 sees actuals when it resolves expect supertypes) => they compare the scopes differently.
    // I don't think we can fix this 'K1 green -> K2 red'. It must be a rare case anyway.
    override fun foo(): List<String> {
        return super.foo()
    }
}

// FILE: BaseJava.java
import java.util.List;

public class BaseJava {
    public List<String> foo() {
        return null;
    }
}
