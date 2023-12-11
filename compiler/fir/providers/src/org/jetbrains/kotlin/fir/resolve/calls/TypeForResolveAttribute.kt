/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls

import org.jetbrains.kotlin.fir.types.ConeAttributeWithConeType
import org.jetbrains.kotlin.fir.types.ConeAttributes
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.renderForDebugging
import kotlin.reflect.KClass

class TypeForResolveAttribute(override val coneType: ConeKotlinType) : ConeAttributeWithConeType<TypeForResolveAttribute>() {
    override fun union(other: TypeForResolveAttribute?): TypeForResolveAttribute? = null
    override fun intersect(other: TypeForResolveAttribute?): TypeForResolveAttribute? = null
    override fun add(other: TypeForResolveAttribute?): TypeForResolveAttribute = other ?: this
    override fun isSubtypeOf(other: TypeForResolveAttribute?): Boolean = true
    override fun copyWith(newType: ConeKotlinType): TypeForResolveAttribute = TypeForResolveAttribute(newType)

    override fun toString(): String {
        return "For Resolution(${coneType.renderForDebugging()})"
    }

    override val key: KClass<out TypeForResolveAttribute> get() = TypeForResolveAttribute::class
    override val keepInInferredDeclarationType: Boolean get() = true
}

val ConeAttributes.forResolve: TypeForResolveAttribute? by ConeAttributes.attributeAccessor<TypeForResolveAttribute>()

fun ConeKotlinType.forResolveOrSelf(): ConeKotlinType = attributes.forResolve?.coneType ?: this
