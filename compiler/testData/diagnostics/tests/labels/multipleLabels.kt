fun foo1() {
    l2@ l1@ do {
        println()
    } while (true)
}

fun foo2() {
    l4@ _@ l3@ __@ l2@ l1@ while (true) {
        println()
    }
}

fun foo3() {
    l2@ l1@ 42
}

fun foo4() {
    l1@ do {
        l4@ { false }
    } while (true)
}

fun foo5() {
    l2@ l1@ do {
        l4@ { false }
    } while (true)
}

fun foo6() {
    l2@ l1@ do {
        l4@ l3@{ true }
    } while (true)
}

fun foo7() {
    l3@ l2@ l1@ fun bar() {}
}