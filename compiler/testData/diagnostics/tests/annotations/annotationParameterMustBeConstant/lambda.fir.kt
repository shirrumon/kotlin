// ISSUE: KT-59565

@Target(AnnotationTarget.TYPE)
annotation class Ann(val x: Int)

class A<T: @Ann(<!ARGUMENT_TYPE_MISMATCH!>{}<!>) Any>
fun f(x: @Ann(<!ARGUMENT_TYPE_MISMATCH!>{}<!>) Int) = x