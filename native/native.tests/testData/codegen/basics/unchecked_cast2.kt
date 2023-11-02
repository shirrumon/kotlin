fun box(): String {
    try {
        val x = cast<String>(Any())
        return "FAIL: ${x.length}"
    } catch (e: ClassCastException) {
        return "OK"
    }
}

fun <T> cast(x: Any?) = x as T