// TARGET_BACKEND: JVM
// IGNORE_BACKEND_K1: JVM, JVM_IR, JS_IR, JS_IR_ES6
// IGNORE_LIGHT_ANALYSIS
// WITH_STDLIB

// MODULE: lib
// FILE: script.kts

fun ok() = "OK"

// MODULE: main(lib)
// FILE: test.kt

fun runScriptMethod(name: String, method: String): Any {
    val klass = Thread.currentThread().contextClassLoader.loadClass(name)
    val constructor = klass.constructors.single()
    val instance = constructor.newInstance(emptyArray<String>())
    val method = klass.getMethod(method)
    return method.invoke(instance)
}

fun box(): String =
    runScriptMethod("Script", "ok") as String
