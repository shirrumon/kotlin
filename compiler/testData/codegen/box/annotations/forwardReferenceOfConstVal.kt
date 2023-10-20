// DUMP_IR
// FIR_IDENTICAL

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPE, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER
)
annotation class Ann(val s: String)

@Ann(message)
class Some<@Ann(message) T : @Ann(message) Number> @Ann(message) constructor(
    @Ann(message) a: Int,
    @Ann(message) val b: Int
) {
    fun <@Ann(message) R : @Ann(message) Number> @Ann(message)Any.foo(@Ann(message) x: @Ann(message) Int): @Ann(message) Int {
        return x
    }

    @Ann(message)
    var c: Int
        @Ann(message) get() = 1
        @Ann(message) set(@Ann(message) value) {}

    @Ann(message)
    class Nested
}

const val message = "foo"

fun box(): String = "OK"
