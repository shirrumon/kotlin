// FIR_IDENTICAL
class Key<T>
class Box<T>

fun <T : Any> get(key: Key<in T>): T? = null
fun <T> acceptBox(box: Box<T>) {}

fun <V> test(key: Key<in Box<V>>) {
    get(key)?.let { acceptBox(it) }

    val x = get(key)
    x?.let { acceptBox(it) }
}