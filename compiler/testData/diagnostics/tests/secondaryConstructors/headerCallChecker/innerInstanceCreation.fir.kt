// !DIAGNOSTICS: -UNUSED_PARAMETER
fun foo(x: Outer) = 1
class Outer {
    inner class Inner {
        val prop = 1
    }

    constructor(x: Int)
    constructor(x: Int, y: Int, z: Int = x + Inner().prop <!AMBIGUITY!>+<!> <!NO_THIS!>this<!>.<!UNRESOLVED_REFERENCE!>Inner<!>().<!UNRESOLVED_REFERENCE!>prop<!>) :
        this(x + Inner().prop + this.Inner().prop)
}
