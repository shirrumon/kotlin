fun foo1() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ do {
        println()
    } while (true)
}

fun foo2() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l4@<!> <!UNDERSCORE_IS_RESERVED!>_<!>@ <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l3@<!> <!UNDERSCORE_IS_RESERVED!>__<!>@ <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ while (true) {
        println()
    }
}

fun foo3() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ 42
}

fun foo4() {
    l1@ do {
        l4@ { false }
    } while (true)
}

fun foo5() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ do {
        l4@ { false }
    } while (true)
}

fun foo6() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ do {
        l4@ l3@{ true }
    } while (true)
}

fun foo7() {
    <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l3@<!> <!MULTIPLE_LABELS_ARE_FORBIDDEN!>l2@<!> l1@ fun bar() {}
}
