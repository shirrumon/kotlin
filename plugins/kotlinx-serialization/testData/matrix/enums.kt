// WITH_STDLIB
@file:UseSerializers(EnumWithUseSerializer::class, NestedEnumWithUseSerializer::class, )

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.json.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.reflect.typeOf

enum class EnumWithDef {
    A,
    B,
}

@Serializable
enum class Enum {
    A,
    B,
}

@Serializable(EnumWithCustomSerializer::class)
enum class EnumWithCustom {
    A,
    B,
}

@Serializable(EnumWithCustomClSerializer::class)
enum class EnumWithCustomCl {
    A,
    B,
}

enum class EnumWithContextual {
    A,
    B,
}

enum class EnumWithUse {
    A,
    B,
}

@Serializable
@Extra("Enum1")
enum class Enum1 {
    @Extra("A") A,
    @Extra("B") B,
}

class Container {
    enum class NestedEnumWithDef {
        A,
        B,
    }

    @Serializable
    enum class NestedEnum {
        A,
        B,
    }

    @Serializable(NestedEnumWithCustomSerializer::class)
    enum class NestedEnumWithCustom {
        A,
        B,
    }

    @Serializable(NestedEnumWithCustomClSerializer::class)
    enum class NestedEnumWithCustomCl {
        A,
        B,
    }

    enum class NestedEnumWithContextual {
        A,
        B,
    }

    enum class NestedEnumWithUse {
        A,
        B,
    }

}

class Outer {
}

object EnumWithCustomSerializer: ToDoSerializer<EnumWithCustom>("xEnumWithCustom")
object NestedEnumWithCustomSerializer: ToDoSerializer<Container.NestedEnumWithCustom>("xContainer.NestedEnumWithCustom")
class EnumWithCustomClSerializer: ToDoSerializer<EnumWithCustomCl>("xEnumWithCustomCl")
class NestedEnumWithCustomClSerializer: ToDoSerializer<Container.NestedEnumWithCustomCl>("xContainer.NestedEnumWithCustomCl")

object EnumWithContextualSerializer: ToDoSerializer<EnumWithContextual>("EnumWithContextual")
object NestedEnumWithContextualSerializer: ToDoSerializer<Container.NestedEnumWithContextual>("Container.NestedEnumWithContextual")

class EnumWithUseSerializer: ToDoSerializer<EnumWithUse>("EnumWithUse")
class NestedEnumWithUseSerializer: ToDoSerializer<Container.NestedEnumWithUse>("Container.NestedEnumWithUse")

@kotlinx.serialization.SerialInfo
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class Extra(val value: String)

fun box(): String {
    val module = SerializersModule {
        contextual(EnumWithContextualSerializer)
        contextual(NestedEnumWithContextualSerializer)
    }
    
    serializer<EnumWithDef>().checkElements("A", "B")
    serializer<Container.NestedEnumWithDef>().checkElements("A", "B")
    serializer<Enum>().checkElements("A", "B")
    serializer<Container.NestedEnum>().checkElements("A", "B")
    serializer<EnumWithCustom>().checkElements("A", "B")
    serializer<Container.NestedEnumWithCustom>().checkElements("A", "B")
    serializer<EnumWithCustomCl>().checkElements("A", "B")
    serializer<Container.NestedEnumWithCustomCl>().checkElements("A", "B")
    serializer<EnumWithContextual>().checkElements("A", "B")
    serializer<Container.NestedEnumWithContextual>().checkElements("A", "B")
    serializer<EnumWithUse>().checkElements("A", "B")
    serializer<Container.NestedEnumWithUse>().checkElements("A", "B")
    serializer<Enum1>().checkElements("A", "B")
    
    // Call serializer factory function in companion
    Enum.serializer().checkSerialName("Enum")?.let { return it }
    Container.NestedEnum.serializer().checkSerialName("Container.NestedEnum")?.let { return it }
    EnumWithCustom.serializer().checkSerialName("xEnumWithCustom")?.let { return it }
    Container.NestedEnumWithCustom.serializer().checkSerialName("xContainer.NestedEnumWithCustom")?.let { return it }
    EnumWithCustomCl.serializer().checkSerialName("xEnumWithCustomCl")?.let { return it }
    Container.NestedEnumWithCustomCl.serializer().checkSerialName("xContainer.NestedEnumWithCustomCl")?.let { return it }
    Enum1.serializer().checkSerialName("Enum1")?.let { return it }
    
    // Serializer lookup by generic parameter
    serializer<EnumWithDef>().checkSerialName("EnumWithDef")?.let { return it }
    serializer<Container.NestedEnumWithDef>().checkSerialName("Container.NestedEnumWithDef")?.let { return it }
    serializer<Enum>().checkSerialName("Enum")?.let { return it }
    serializer<Container.NestedEnum>().checkSerialName("Container.NestedEnum")?.let { return it }
    serializer<EnumWithCustom>().checkSerialName("xEnumWithCustom")?.let { return it }
    serializer<Container.NestedEnumWithCustom>().checkSerialName("xContainer.NestedEnumWithCustom")?.let { return it }
    serializer<EnumWithCustomCl>().checkSerialName("xEnumWithCustomCl")?.let { return it }
    serializer<Container.NestedEnumWithCustomCl>().checkSerialName("xContainer.NestedEnumWithCustomCl")?.let { return it }
    serializer<EnumWithUse>().checkSerialName("EnumWithUse")?.let { return it }
    serializer<Container.NestedEnumWithUse>().checkSerialName("Container.NestedEnumWithUse")?.let { return it }
    serializer<Enum1>().checkSerialName("Enum1")?.let { return it }
    
    // Serializer lookup by typeOf function
    serializer(typeOf<EnumWithDef>()).checkSerialName("EnumWithDef")?.let { return it }
    serializer(typeOf<Container.NestedEnumWithDef>()).checkSerialName("Container.NestedEnumWithDef")?.let { return it }
    serializer(typeOf<Enum>()).checkSerialName("Enum")?.let { return it }
    serializer(typeOf<Container.NestedEnum>()).checkSerialName("Container.NestedEnum")?.let { return it }
    serializer(typeOf<EnumWithCustom>()).checkSerialName("xEnumWithCustom")?.let { return it }
    serializer(typeOf<Container.NestedEnumWithCustom>()).checkSerialName("xContainer.NestedEnumWithCustom")?.let { return it }
    serializer(typeOf<EnumWithCustomCl>()).checkSerialName("xEnumWithCustomCl")?.let { return it }
    serializer(typeOf<Container.NestedEnumWithCustomCl>()).checkSerialName("xContainer.NestedEnumWithCustomCl")?.let { return it }
    serializer(typeOf<EnumWithUse>()).checkSerialName("EnumWithUse")?.let { return it }
    serializer(typeOf<Container.NestedEnumWithUse>()).checkSerialName("Container.NestedEnumWithUse")?.let { return it }
    serializer(typeOf<Enum1>()).checkSerialName("Enum1")?.let { return it }
    
    // Serializer lookup by generic parameter in custom module
    module.serializer<EnumWithDef>().checkSerialName("EnumWithDef")?.let { return it }
    module.serializer<Container.NestedEnumWithDef>().checkSerialName("Container.NestedEnumWithDef")?.let { return it }
    module.serializer<Enum>().checkSerialName("Enum")?.let { return it }
    module.serializer<Container.NestedEnum>().checkSerialName("Container.NestedEnum")?.let { return it }
    module.serializer<EnumWithCustom>().checkSerialName("xEnumWithCustom")?.let { return it }
    module.serializer<Container.NestedEnumWithCustom>().checkSerialName("xContainer.NestedEnumWithCustom")?.let { return it }
    module.serializer<EnumWithCustomCl>().checkSerialName("xEnumWithCustomCl")?.let { return it }
    module.serializer<Container.NestedEnumWithCustomCl>().checkSerialName("xContainer.NestedEnumWithCustomCl")?.let { return it }
    module.serializer<EnumWithUse>().checkSerialName("EnumWithUse")?.let { return it }
    module.serializer<Container.NestedEnumWithUse>().checkSerialName("Container.NestedEnumWithUse")?.let { return it }
    module.serializer<Enum1>().checkSerialName("Enum1")?.let { return it }
    
    // Serializer lookup by typeOf function in custom module
    module.serializer(typeOf<EnumWithDef>()).checkSerialName("EnumWithDef")?.let { return it }
    module.serializer(typeOf<Container.NestedEnumWithDef>()).checkSerialName("Container.NestedEnumWithDef")?.let { return it }
    module.serializer(typeOf<Enum>()).checkSerialName("Enum")?.let { return it }
    module.serializer(typeOf<Container.NestedEnum>()).checkSerialName("Container.NestedEnum")?.let { return it }
    module.serializer(typeOf<EnumWithCustom>()).checkSerialName("xEnumWithCustom")?.let { return it }
    module.serializer(typeOf<Container.NestedEnumWithCustom>()).checkSerialName("xContainer.NestedEnumWithCustom")?.let { return it }
    module.serializer(typeOf<EnumWithCustomCl>()).checkSerialName("xEnumWithCustomCl")?.let { return it }
    module.serializer(typeOf<Container.NestedEnumWithCustomCl>()).checkSerialName("xContainer.NestedEnumWithCustomCl")?.let { return it }
    module.serializer(typeOf<EnumWithUse>()).checkSerialName("EnumWithUse")?.let { return it }
    module.serializer(typeOf<Container.NestedEnumWithUse>()).checkSerialName("Container.NestedEnumWithUse")?.let { return it }
    module.serializer(typeOf<Enum1>()).checkSerialName("Enum1")?.let { return it }
    
    // Annotation on type should have value same as a class name
    serializer<Enum1>().checkAnnotation("Enum1")
    
    // Annotation on enum entries should have value same as a entry names
    serializer<Enum1>().checkElementAnnotations("A", "B")
    
    return "OK"
}

fun KSerializer<*>.checkSerialName(name: String): String? = if (descriptor.serialName != name) descriptor.serialName else null
fun KSerializer<*>.checkAnnotation(value: String): String? = descriptor.annotations.filterIsInstance<Extra>().single().value.let { if (it != value) it else null }

fun KSerializer<*>.checkElementAnnotations(vararg values: String): String? = (0 ..< descriptor.elementsCount).map { descriptor.getElementAnnotations(it).filterIsInstance<Extra>().single().value}.let { if (it != values.toList()) it.toString() else null }

fun KSerializer<*>.checkElements(vararg values: String): String? = (0 ..< descriptor.elementsCount).map { descriptor.getElementName(it) }.let { if (it != values.toList()) it.toString() else null }


abstract class ToDoSerializer<T: Any>(descriptorName: String): KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(descriptorName, PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): T = TODO()
    override fun serialize(encoder: Encoder, value: T) = TODO()
}

