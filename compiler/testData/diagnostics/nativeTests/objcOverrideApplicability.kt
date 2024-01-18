// !API_VERSION: 2.0
// FIR_IDENTICAL
// WITH_PLATFORM_LIBS

import kotlinx.cinterop.*
import platform.darwin.*

<!INAPPLICABLE_OBJC_OVERRIDE!>@ExperimentalObjCOverride<!>
fun foo() = 1

class A {
    <!INAPPLICABLE_OBJC_OVERRIDE!>@ExperimentalObjCOverride<!>
    fun foo() = 1
}

<!WRONG_ANNOTATION_TARGET!>@ExperimentalObjCOverride<!>
class B : NSObject() {
    <!INAPPLICABLE_OBJC_OVERRIDE!>@ExperimentalObjCOverride<!>
    fun foo() = 1
    <!WRONG_ANNOTATION_TARGET!>@ExperimentalObjCOverride<!>
    val v = 1
}
