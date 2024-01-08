/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix

import org.jetbrains.kotlinx.serialization.matrix.SerializerKind.BY_DEFAULT

internal fun hasFactoryFun(type: TypeVariant): Boolean {
    return type.features.serializer in setOf(
        SerializerKind.GENERATED,
        SerializerKind.CUSTOM_OBJECT,
        SerializerKind.CUSTOM_CLASS
    )
}

internal fun isUsedInLookup(type: TypeVariant): Boolean {
    return type.features.serializer in setOf(
        SerializerKind.GENERATED,
        SerializerKind.CUSTOM_OBJECT,
        SerializerKind.CUSTOM_CLASS,
        SerializerKind.CLASS_USE_SERIALIZER
    ) || (type is EnumVariant && type.features.serializer == BY_DEFAULT)
}

internal fun hasContextualSerializer(type: TypeVariant): Boolean {
    return type.features.serializer in setOf(SerializerKind.CONTEXTUAL)
}

internal fun hasAnnotationOnType(type: TypeVariant): Boolean {
    return type is EnumVariant && SerialInfo.ON_TYPE in type.options.serialInfo
}

internal fun hasAnnotationOnElement(type: TypeVariant): Boolean {
    return type is EnumVariant && SerialInfo.ON_ELEMENTS in type.options.serialInfo
}
