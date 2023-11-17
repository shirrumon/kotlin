// IGNORE_BACKEND_K2: JVM_IR

fun box() = expectThrowableMessage {
    assert("Hello, World".matches("[A-Za-z]+".toRegex()))
}
