// FIR_IDENTICAL
// DIAGNOSTICS: -DEBUG_INFO_SMARTCAST

class Generic<T> {
    fun bar(arr: Array<out T?>) {
        for (x in arr) {
            if (x == null) continue
            baz(x)
        }
    }

    fun baz(x: T) {}
}

class NonGeneric {
    fun bar(arr: Array<out String?>) {
        for (x in arr) {
            if (x == null) continue
            baz(x)
        }
    }

    fun baz(x: String) {}
}

class Intersection {
    fun bar(arr: Array<out CharSequence>) {
        for (x in arr) {
            if (x !is Number) continue
            baz(x)
            baz2(x)
        }
    }

    fun baz(x: CharSequence) {}
    fun baz2(x: Number) {}
}