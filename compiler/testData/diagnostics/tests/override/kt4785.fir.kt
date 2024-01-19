interface T {
    fun foo()
}

open class C {
    protected fun foo() {}
}

class <!CANNOT_WEAKEN_ACCESS_PRIVILEGE!>E<!> : C(), T

val z: T = <!CANNOT_WEAKEN_ACCESS_PRIVILEGE!>object<!> : C(), T {}
