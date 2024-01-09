/* e_remainderf.c -- float version of e_remainder.c.
 * Conversion to float by Ian Lance Taylor, Cygnus Support, ian@cygnus.com.
 */

/*
 * ====================================================
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunPro, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * ====================================================
 */

package kotlin.math.fdlibm

import kotlin.wasm.internal.wasm_f32_abs

private const val zero = 0.0f

//private const val FLT_UWORD_HALF_MAX = 0x7f7fffff
//private fun FLT_UWORD_IS_FINITE(@Suppress("UNUSED_PARAMETER") x: Int): Boolean = true
//private fun FLT_UWORD_IS_NAN(@Suppress("UNUSED_PARAMETER") x: Int): Boolean = false
//private fun FLT_UWORD_IS_ZERO(@Suppress("UNUSED_PARAMETER") x: Int): Boolean = false

private const val FLT_UWORD_HALF_MAX = 0x7effffff
private fun FLT_UWORD_IS_FINITE(x: Int): Boolean = x < 0x7f800000
private fun FLT_UWORD_IS_NAN(x: Int): Boolean = x > 0x7f800000
private fun FLT_UWORD_IS_ZERO(x: Int): Boolean = x == 0x7f800000

internal fun __ieee754_remainderf(_x: Float, _p: Float): Float {
    var x = _x
    var p = _p
    var hx: Int
    var hp: Int
    val sx: UInt
    val p_half: Float

    hx = x.toBits()
    hp = p.toBits()

    sx = (hx and 0x80000000.toInt()).toUInt()
    hp = hp and 0x7fffffff
    hx = hx and 0x7fffffff

    /* purge off exception values */

    if (FLT_UWORD_IS_ZERO(hp) || !FLT_UWORD_IS_FINITE(hx) || FLT_UWORD_IS_NAN(hp))
        return (x * p) / (x * p)

    if (hp <= FLT_UWORD_HALF_MAX)
        x = __ieee754_fmodf(x, p + p)    /* now x < 2p */
    if ((hx - hp) == 0)
        return zero * x
    x = wasm_f32_abs(x)
    p = wasm_f32_abs(p)
    if (hp < 0x01000000) {
        if (x + x > p) {
            x -= p
            if (x + x >= p)
                x -= p
        }
    } else {
        p_half = 0.5f * p

        if (x > p_half) {
            x -= p
            if (x >= p_half)
                x -= p
        }
    }

    hx = x.toBits()
    x = Float.fromBits(hx xor sx.toInt())
    return x
}