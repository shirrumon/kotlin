// TARGET_BACKEND: JVM
// MUTE_SIGNATURE_COMPARISON_K2: JVM_IR
// FILE: Foo.java
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GetNullable {
    private Integer foo = 1;

    @Nullable
    Integer getFoo() {
        return foo;
    }

    void setFoo(Integer newFoo) {
        foo = newFoo;
    }
}

public class SetNullable {
    private Integer foo = 1;

    Integer getFoo() {
        return foo;
    }

    void setFoo(@Nullable Integer newFoo) {
        foo = newFoo;
    }
}

public class GetNullableSetNotNull {
    private Integer foo = 1;

    @Nullable
    Integer getFoo() {
        return foo;
    }

    void setFoo(@NotNull Integer newFoo) {
        foo = newFoo;
    }
}

public class SetNullableGetNotNull {
    private Integer foo = 1;

    @NotNull
    Integer getFoo() {
        return foo;
    }

    void setFoo(@Nullable Integer newFoo) {
        foo = newFoo;
    }
}

// FILE: main.kt
fun funGetNullableGet(foo: GetNullable) = foo::getFoo
fun funGetNullableSet(foo: GetNullable) = foo::setFoo
fun funGetNullableField(foo: GetNullable) = foo.foo

fun funSetNullableGet(foo: SetNullable) = foo::getFoo
fun funSetNullableSet(foo: SetNullable) = foo::setFoo
fun funSetNullableField(foo: SetNullable) = foo.foo

fun funGetNullableSetNotNullGet(foo: GetNullableSetNotNull) = foo::getFoo
fun funGetNullableSetNotNullSet(foo: GetNullableSetNotNull) = foo::setFoo
fun funGetNullableSetNotNullField(foo: GetNullableSetNotNull) = foo.foo

fun funSetNullableGetNotNullGet(foo: SetNullableGetNotNull) = foo::getFoo
fun funSetNullableGetNotNullSet(foo: SetNullableGetNotNull) = foo::setFoo
fun funSetNullableGetNotNullField(foo: SetNullableGetNotNull) = foo.foo
