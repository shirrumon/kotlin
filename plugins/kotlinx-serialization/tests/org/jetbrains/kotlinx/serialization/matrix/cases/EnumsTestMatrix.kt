/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.matrix.cases

import org.jetbrains.kotlinx.serialization.matrix.SerializerKind.*
import org.jetbrains.kotlinx.serialization.matrix.TypeLocation.FILE_ROOT
import org.jetbrains.kotlinx.serialization.matrix.TypeLocation.NESTED
import org.jetbrains.kotlinx.serialization.matrix.*
import org.jetbrains.kotlinx.serialization.matrix.impl.*
import org.jetbrains.kotlinx.serialization.matrix.impl.elements
import org.jetbrains.kotlinx.serialization.matrix.impl.serialName

fun CombinationContext.enumsTestMatrix() {
    val enums = defineEnums(
        SerializerKind.entries.toSet(),
        setOf(FILE_ROOT, NESTED)
    ) {
        entries("A", "B")
        descriptorAccessing(*DescriptorAccessing.entries.toTypedArray())
    }

    val enumsWithAnnotations = defineEnums(
        setOf(GENERATED),
        setOf(FILE_ROOT)
    ) {
        entries("A", "B")
        serialInfo(*SerialInfo.entries.toTypedArray())
    }

    val allTypes = enums + enumsWithAnnotations

    val withCompanion = allTypes.filter(::hasFactoryFun)
    val lookupTypes = allTypes.filter(::isUsedInLookup)
    val contextual = allTypes.filter(::hasContextualSerializer)

    val annotatedTypes = allTypes.filter(::hasAnnotationOnType)
    val annotatedElementsTypes = allTypes.filter(::hasAnnotationOnElement)

    box() {
        line("val module = SerializersModule {")
        contextual.forEach { type ->
            line("    contextual(${type.named.contextualSerializer})")
        }
        line("}")
        line()

        allTypes.forEach { type ->
            line("serializer<${type.named.classUsage}>().checkElements(${
                type.elements.joinToString(", ") { "\"$it\"" }
            })")
        }
        line()

        line("// Call serializer factory function in companion")
        withCompanion.forEach { type ->
            line("${type.named.classUsage}.serializer().checkSerialName(\"${type.named.serialName}\")?.let { return it }")
        }
        line()

        line("// Serializer lookup by generic parameter")
        lookupTypes.forEach { type ->
            line("serializer<${type.named.classUsage}>().checkSerialName(\"${type.named.serialName}\")?.let { return it }")
        }
        line()

        line("// Serializer lookup by typeOf function")
        lookupTypes.forEach { type ->
            line("serializer(typeOf<${type.named.classUsage}>()).checkSerialName(\"${type.named.serialName}\")?.let { return it }")
        }
        line()

        line("// Serializer lookup by generic parameter in custom module")
        lookupTypes.forEach { type ->
            line("module.serializer<${type.named.classUsage}>().checkSerialName(\"${type.named.serialName}\")?.let { return it }")
        }
        line()

        line("// Serializer lookup by typeOf function in custom module")
        lookupTypes.forEach { type ->
            line("module.serializer(typeOf<${type.named.classUsage}>()).checkSerialName(\"${type.named.serialName}\")?.let { return it }")
        }
        line()

        line("// Annotation on type should have value same as a class name")
        annotatedTypes.forEach { type ->
            line("serializer<${type.named.classUsage}>().checkAnnotation(\"${type.named.classUsage}\")")
        }
        line()

        line("// Annotation on enum entries should have value same as a entry names")
        annotatedElementsTypes.forEach { type ->
            line("serializer<${type.named.classUsage}>().checkElementAnnotations(${
                type.elements.joinToString(", ") { "\"$it\"" }
            })")
        }
        line()
    }
}