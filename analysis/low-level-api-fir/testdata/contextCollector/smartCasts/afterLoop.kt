interface Node {
    val shouldProcess: Boolean
    val parent: Node?
}

fun test(initial: Node?) {
    var current = initial

    while (initial!!.shouldProcess) {
        consume(current)
        current = current.parent
    }

    <expr>consume(initial)</expr>
}

fun consume(node: Node) {}