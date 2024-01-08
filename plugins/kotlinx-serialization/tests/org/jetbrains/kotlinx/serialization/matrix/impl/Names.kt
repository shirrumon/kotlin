/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix.impl

import org.jetbrains.kotlinx.serialization.matrix.*
import org.jetbrains.kotlinx.serialization.matrix.SerializerKind.*

internal const val CLASS_FOR_NESTED = "Container"

internal const val CLASS_FOR_INNER = "Outer"

internal const val SERIAL_ANNOTATION = "Extra"


internal val TypeVariant.className: String
    get() = when (this) {
        is EnumVariant -> features.location.namePart + "Enum" + features.serializer.namePart
    }

internal val TypeVariant.elements: List<String>
    get() = when (this) {
        is EnumVariant -> options.entries.toList()
    }

internal val NamedTypeVariant.serialName: String
    get() {
        return if (variant.features.serializer == CUSTOM_CLASS || variant.features.serializer == CUSTOM_OBJECT) {
            // serial name in custom serializers start with "x..."
            "x$classUsage"
        } else {
            // in all other cases serial name is simple class name
            classUsage
        }
    }


internal val NamedTypeVariant.useSerializer: String?
    get() {
        if (variant.features.serializer != CLASS_USE_SERIALIZER) return null
        return name + "Serializer"
    }

internal val NamedTypeVariant.customSerializer: String
    get() = when (variant.features.serializer) {
        CUSTOM_CLASS, CUSTOM_OBJECT -> name + "Serializer"
        else -> throw Exception("")
    }

internal val NamedTypeVariant.contextualSerializer: String
    get() = when (variant.features.serializer) {
        CONTEXTUAL -> name + "Serializer"
        else -> throw Exception("")
    }

private val TypeLocation.namePart: String
    get() = when (this) {
        TypeLocation.FILE_ROOT -> ""
        TypeLocation.LOCAL -> "Local"
        TypeLocation.NESTED -> "Nested"
        TypeLocation.INNER -> "Inner"
    }

private val SerializerKind.namePart: String
    get() = when (this) {
        BY_DEFAULT -> "WithDef"
        GENERATED -> ""
        CUSTOM_OBJECT -> "WithCustom"
        CUSTOM_CLASS -> "WithCustomCl"
        CONTEXTUAL -> "WithContextual"
        CLASS_USE_SERIALIZER -> "WithUse"
    }
