// ISSUE: KT-59565

@Target(AnnotationTarget.TYPE)
annotation class Ann(val x: Int)

class A<T: @Ann(<!ANNOTATION_ARGUMENT_MUST_BE_CONST, TYPE_MISMATCH!>{}<!>) Any>
fun f(x: @Ann(<!ANNOTATION_ARGUMENT_MUST_BE_CONST, TYPE_MISMATCH!>{}<!>) Int) = x