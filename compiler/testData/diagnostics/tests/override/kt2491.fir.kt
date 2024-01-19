interface T {
    public fun foo()
}

open class C {
    protected fun foo() {}
}

class <!CANNOT_WEAKEN_ACCESS_PRIVILEGE!>D<!> : C(), T

val obj: C = <!CANNOT_WEAKEN_ACCESS_PRIVILEGE!>object<!> : C(), T {}
