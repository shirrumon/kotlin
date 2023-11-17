// IGNORE_BACKEND_K2: JVM_IR

fun box() = expectThrowableMessage {
    assert("Hello" !in listOf("Hello", "World"))
}
