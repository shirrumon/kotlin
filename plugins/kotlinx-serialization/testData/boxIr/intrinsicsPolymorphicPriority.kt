// TARGET_BACKEND: JVM_IR

// WITH_STDLIB

// FILE: stub.kt

@file:JvmName("SerializersKt")

package kotlinx.serialization

import kotlin.reflect.KClass
import kotlinx.serialization.modules.*

// Copy of runtime function from kotlinx-serialization 1.7.0
fun moduleThenPolymorphic(module: SerializersModule, kClass: KClass<*>): KSerializer<*> {
    return module.getContextual(kClass) ?: PolymorphicSerializer(kClass)
}

// FILE: test.kt

package a

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*
import kotlin.reflect.KClass
import kotlin.test.*

interface IApiError {
    val code: Int
}

object MyApiErrorSerializer : KSerializer<IApiError> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IApiError", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: IApiError) {
        TODO()
    }

    override fun deserialize(decoder: Decoder): IApiError {
        TODO()
    }
}

fun box(): String {
    val module = serializersModuleOf(IApiError::class, MyApiErrorSerializer)
    assertSame(MyApiErrorSerializer, module.serializer<IApiError>() as KSerializer<IApiError>)
    assertEquals(
        MyApiErrorSerializer.descriptor,
        module.serializer<List<IApiError>>().descriptor.elementDescriptors.first()
    )
    return "OK"
}
