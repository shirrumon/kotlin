// ISSUE: KT-59355

// MODULE: common
internal expect open class <!NO_ACTUAL_FOR_EXPECT!>Some<!> {
    protected class ProtectedNested
    internal class InternalNested

    public fun publicFun()
    internal fun internalFun()
    protected fun protectedFun()
}

internal expect open class <!NO_ACTUAL_FOR_EXPECT!>Other<!> {
    protected class ProtectedNested
    internal class InternalNested
}

// MODULE: platform-jvm()()(common)
<!ACTUAL_WITHOUT_EXPECT!>public<!> actual open class Some { // should be allowed
    <!ACTUAL_WITHOUT_EXPECT!>public<!> class <!ACTUAL_MISSING!>ProtectedNested<!>  // should be allowed
    <!ACTUAL_WITHOUT_EXPECT!>public<!> class <!ACTUAL_MISSING!>InternalNested<!> // should be allowed

    public actual fun publicFun() {} // should be allowed
    public actual fun internalFun() {} // shouldn't be allowed (not reported in K1, because actual isn't matched with expect)
    public actual fun protectedFun() {} // shouldn't be allowed (not reported in K1, because actual isn't matched with expect)
}

public open class PlatformOther { // should be allowed
    public class ProtectedNested  // should be allowed
    public class InternalNested // should be allowed
}

<!ACTUAL_WITHOUT_EXPECT!>internal<!> actual typealias Other = PlatformOther // should be allowed

