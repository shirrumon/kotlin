// FILE: main.kt
package com.test

class A(val a: Int = 3, val b: String = "foo", val c: List<String> = listOf("foo", "bar"), val d: List<String>)

fun foo(a: Int = 3, b: String = "foo", c: List<String> = listOf("foo", "bar"), d: Boolean, e: Float) = c.joinToString() + b + "$a $d $e"

fun bar(): String {
    val a = A(c = listOf("a", "b"), d = emptyList())
    return foo(a.a, a.b, a.c, false, 1.3f)
}