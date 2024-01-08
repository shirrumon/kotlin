/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix

import org.jetbrains.kotlinx.serialization.matrix.impl.CLASS_FOR_INNER
import org.jetbrains.kotlinx.serialization.matrix.impl.CLASS_FOR_NESTED


interface TypeContainer<T : TypeVariant> : Iterable<T> {
    operator fun plus(other: TypeContainer<T>): TypeContainer<T>

    fun filter(predicate: (TypeVariant) -> Boolean): TypeContainer<T>

    fun filterEnums(predicate: (EnumVariant) -> Boolean = { true }): TypeContainer<EnumVariant>
}

/**
 * A combination of mutually exclusive Kotlin language and serialization features.
 */
abstract class TypeFeatures {
    abstract val serializer: SerializerKind
    abstract val location: TypeLocation
}

/**
 * Description of the type used to generate type definitions and their uses.
 *
 * Parent type.
 */
sealed class TypeVariant {
    /**
     * A combination of mutually exclusive Kotlin language and serialization features.
     */
    abstract val features: TypeFeatures
}

/**
 * Description of the enum types used to generate type definitions and their uses.
 */
data class EnumVariant(
    override val features: EnumFeatures,
    val options: EnumOptions,
) : TypeVariant()

/**
 * A combination of mutually exclusive Kotlin language and serialization features for enum types.
 */
data class EnumFeatures(
    override val serializer: SerializerKind,
    override val location: TypeLocation,
) : TypeFeatures()

/**
 * Optional features for the type.
 */
data class EnumOptions(
    val serialInfo: Set<SerialInfo>,
    val descriptorAccessing: Set<DescriptorAccessing>,
    val entries: Set<String>,
)


enum class SerializerKind : TypeFeature {
    /**
     * Serializable by default (Without @Serializable annotation: enum, interface, sealed interface)
     */
    BY_DEFAULT,

    /**
     * @Serializable
     */
    GENERATED,

    /**
     * @Serializable(CustomObjectSerializer::class)
     */
    CUSTOM_OBJECT,

    /**
     * @Serializable(CustomSerializer::class)
     */
    CUSTOM_CLASS,

    /**
     * Contextual by SerialModule.
     */
    CONTEXTUAL,

    /**
     * Serializable by @UseSerializers
     */
    CLASS_USE_SERIALIZER
}

/**
 * Location of the type definition.
 */
enum class TypeLocation : TypeFeature {
    FILE_ROOT,
    LOCAL,
    NESTED,
    INNER
}

enum class SerialInfo : TypeFeature {
    ON_TYPE,
    ON_ELEMENTS
}


/**
 * Kotlin and serialization feature that are not mutually exclusive and there may be several features of the same kind.
 */
interface TypeOptionalFeature

enum class DescriptorAccessing : TypeOptionalFeature {
    FROM_INIT,
    FROM_COMPANION_INIT,
    FROM_COMPANION_PROPERTY_INIT
}

interface TypeFeature




/**
 * A pair in the description of the type and the name that was assigned to it during generation.
 */
class NamedTypeVariant(val name: String, val variant: TypeVariant) {
    internal val classUsage: String
        get() {
            return when (variant.features.location) {
                TypeLocation.FILE_ROOT -> name
                TypeLocation.LOCAL -> name
                TypeLocation.NESTED -> "$CLASS_FOR_NESTED.$name"
                TypeLocation.INNER -> "$CLASS_FOR_INNER.$name"
            }
        }
}
