/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix.impl

import org.jetbrains.kotlinx.serialization.matrix.EnumVariant
import org.jetbrains.kotlinx.serialization.matrix.TypeVariant
import org.jetbrains.kotlinx.serialization.matrix.TypeContainer

internal data class TypeContainerImpl<T : TypeVariant>(val types: Set<T>) : TypeContainer<T> {
    override fun plus(other: TypeContainer<T>): TypeContainerImpl<T> {
        return TypeContainerImpl(types + other)
    }

    override fun filter(predicate: (TypeVariant) -> Boolean): TypeContainer<T> {
        return TypeContainerImpl(types.filter(predicate).toSet())
    }

    override fun filterEnums(predicate: (EnumVariant) -> Boolean): TypeContainer<EnumVariant> {
        val filtered = types.filterIsInstance<EnumVariant>().filter { variant -> predicate(variant) }
        return TypeContainerImpl(filtered.toSet())
    }

    override fun iterator(): Iterator<T> {
        return types.iterator()
    }
}

