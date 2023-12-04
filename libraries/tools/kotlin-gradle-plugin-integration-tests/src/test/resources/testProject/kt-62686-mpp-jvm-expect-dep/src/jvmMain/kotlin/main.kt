
actual class A {
    actual fun foo(x: Base) = "aloha"
    fun foo(x: Child) = 42
}

fun foo(x: Child) = 42

fun testPlatform() =
    A().foo(Child())

fun main() {
    println(test())
    println(testPlatform())
}
