// MODULE: m1-common
// FILE: common.kt

expect open class Base {
    <!INCOMPATIBLE_MATCHING{JVM}!>open fun foo(): MutableList<String><!>
}

<!INCOMPATIBLE_MATCHING{JVM}!>expect open class Foo : Base {

}<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual typealias Base = BaseJava

actual open class Foo : Base() {
    // False negative RETURN_TYPE_CHANGED_IN_NON_FINAL_EXPECT_CLASSIFIER_ACTUALIZATION diagnostic
    // - K1 doesn't report a diagnostic here because when it compares scopes it sees flexible type
    // - K2 doesn't report a diagnostic here because K2 doesn't compare return types on frontend.
    //   It reports INCOMPATIBLE_MATCHING on backend instead KT-60961.
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
