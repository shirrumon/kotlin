/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix.impl

import org.jetbrains.kotlinx.serialization.matrix.EnumVariant
import org.jetbrains.kotlinx.serialization.matrix.NamedTypeVariant
import org.jetbrains.kotlinx.serialization.matrix.SerialInfo
import org.jetbrains.kotlinx.serialization.matrix.SerializerKind.*
import org.jetbrains.kotlinx.serialization.matrix.TypeLocation.*

/*

<file level declarations>

class Container {
    <nested declarations>
}

class Outer {
    <inner declarations>
}

fun <function name> () {
    <local classes>
    <function declaration>
}

*/
internal fun Appendable.writeTypes(types: List<NamedTypeVariant>) {
    // file-level
    types.filter { type -> type.variant.features.location == FILE_ROOT }.forEach { type ->
        writeTypeDef(type)
    }

    // nested
    append("class ")
    append(CLASS_FOR_NESTED)
    append(" {\n")
    types.filter { named -> named.variant.features.location == NESTED }.forEach { type ->
        writeTypeDef(type, "    ")
    }
    append("}\n\n")

    // write inner
    append("class ")
    append(CLASS_FOR_INNER)
    append(" {\n")
    types.filter { named -> named.variant.features.location == LOCAL }.forEach { type ->
        writeTypeDef(type, "    ")
    }
    append("}\n\n")

    // write custom serializers
    types.forEach { type ->
        writeCustomSerializer(type)
    }

    appendLine()

    // write contextual serializers
    types.forEach { type ->
        writeContextualSerializer(type)
    }

    appendLine()

    // write use serializers
    types.forEach { type ->
        writeUseSerializer(type)
    }

    appendLine()

    writeSerialInfo()

    appendLine()
}

internal fun Appendable.writeHeader(types: List<NamedTypeVariant>) {
    appendLine("// WITH_STDLIB")
    writeUseSerializers(types)
    appendLine()
    appendLine("import kotlinx.serialization.*")
    appendLine("import kotlinx.serialization.descriptors.*")
    appendLine("import kotlinx.serialization.json.*")
    appendLine("import kotlinx.serialization.encoding.*")
    appendLine("import kotlinx.serialization.modules.SerializersModule")
    appendLine("import kotlinx.serialization.modules.contextual")
    appendLine("import kotlin.reflect.typeOf")
    appendLine()
}

internal fun Appendable.writeUtils() {
    append("fun KSerializer<*>.checkSerialName(name: String): String? = if (descriptor.serialName != name) descriptor.serialName else null\n")
    append("fun KSerializer<*>.checkAnnotation(value: String): String? = descriptor.annotations.filterIsInstance<Extra>().single().value.let { if (it != value) it else null }\n\n")
    append("fun KSerializer<*>.checkElementAnnotations(vararg values: String): String? = (0 ..< descriptor.elementsCount).map { descriptor.getElementAnnotations(it).filterIsInstance<Extra>().single().value}.let { if (it != values.toList()) it.toString() else null }\n\n")
    append("fun KSerializer<*>.checkElements(vararg values: String): String? = (0 ..< descriptor.elementsCount).map { descriptor.getElementName(it) }.let { if (it != values.toList()) it.toString() else null }\n\n")

    // write abstract serializer
    appendLine(TODO_SERIALIZER)
}




private fun Appendable.writeTypeDef(named: NamedTypeVariant, indent: String = "") {
    when (named.variant) {
        is EnumVariant -> writeEnumDef(named, indent)
    }
}

private fun Appendable.writeEnumDef(named: NamedTypeVariant, indent: String) {
    val enum = named.variant as EnumVariant

    if (enum.features.location == LOCAL || enum.features.location == INNER) {
        // local and inner enums are not allowed
        return
    }

    val classUsage = named.classUsage

    if (enum.features.serializer == GENERATED) {
        append(indent)
        appendLine("@Serializable")
    }
    if (enum.features.serializer == CUSTOM_OBJECT || enum.features.serializer == CUSTOM_CLASS) {
        append(indent)
        appendLine("@Serializable(${named.customSerializer}::class)")
    }

    if (SerialInfo.ON_TYPE in enum.options.serialInfo) {
        append(indent)
        appendLine("@$SERIAL_ANNOTATION(\"$classUsage\")")
    }

    append(indent)
    appendLine("enum class ${named.name} {")
    enum.options.entries.forEach { entry ->
        append(indent)
        append("    ")

        if (SerialInfo.ON_ELEMENTS in enum.options.serialInfo) {
            append("@$SERIAL_ANNOTATION(\"$entry\") ")
        }
        append(entry)
        appendLine(",")
    }

    append(indent)
    appendLine("}")
    appendLine()
}


private fun Appendable.writeUseSerializers(types: List<NamedTypeVariant>) {
    val serializers = types.mapNotNull { type -> type.useSerializer }
    if (serializers.isEmpty()) {
        return
    }
    append("@file:UseSerializers(")
    serializers.forEach { name ->
        append(name)
        append("::class, ")
    }
    appendLine(")")
}

//


private fun Appendable.writeSerialInfo() {
    appendLine("@kotlinx.serialization.SerialInfo")
    appendLine("@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)")
    appendLine("annotation class $SERIAL_ANNOTATION(val value: String)")
}

private fun Appendable.writeCustomSerializer(type: NamedTypeVariant) {
    when (type.variant.features.serializer) {
        CUSTOM_CLASS -> {
            appendLine("class ${type.customSerializer}: ToDoSerializer<${type.classUsage}>(\"${type.serialName}\")")
        }
        CUSTOM_OBJECT -> {
            appendLine("object ${type.customSerializer}: ToDoSerializer<${type.classUsage}>(\"${type.serialName}\")")
        }
        else -> Unit // no-op
    }
}

private fun Appendable.writeContextualSerializer(type: NamedTypeVariant) {
    when (type.variant.features.serializer) {
        CONTEXTUAL -> {
            val usage = type.classUsage
            appendLine("object ${type.contextualSerializer}: ToDoSerializer<$usage>(\"${type.serialName}\")")
        }
        else -> Unit // no-op
    }
}

private fun Appendable.writeUseSerializer(type: NamedTypeVariant) {
    when (type.variant.features.serializer) {
        CLASS_USE_SERIALIZER -> {
            val usage = type.classUsage
            appendLine("class ${type.useSerializer}: ToDoSerializer<$usage>(\"${type.serialName}\")")
        }
        else -> Unit // no-op
    }
}

private const val TODO_SERIALIZER_NAME = "ToDoSerializer"

private const val TODO_SERIALIZER = """
abstract class $TODO_SERIALIZER_NAME<T: Any>(descriptorName: String): KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(descriptorName, PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): T = TODO()
    override fun serialize(encoder: Encoder, value: T) = TODO()
}
"""