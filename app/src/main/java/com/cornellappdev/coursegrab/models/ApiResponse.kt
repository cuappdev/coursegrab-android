package com.cornellappdev.coursegrab.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = ApiResponseSerializer::class)
class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val errors: List<String> = emptyList()
)

class ApiResponseSerializer<T>(
    private val dataSerializer: KSerializer<T>
) : KSerializer<ApiResponse<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ApiResponse") {
        element("success", Boolean.serializer().descriptor)
        element("data", dataSerializer.descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): ApiResponse<T> {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("ApiResponse can only be decoded from JSON")
        val body = input.decodeJsonElement().jsonObject
        val success = body["success"]?.jsonPrimitive?.booleanOrNull ?: false
        val data = body["data"]?.takeUnless { it is JsonNull }

        if (!success) {
            val errors = (data as? JsonObject)?.get("errors")
                ?.let { input.json.decodeFromJsonElement(ListSerializer(String.serializer()), it) }
                .orEmpty()
            return ApiResponse(success = false, errors = errors)
        }

        return ApiResponse(
            success = true,
            data = data?.let { input.json.decodeFromJsonElement(dataSerializer, it) }
        )
    }

    override fun serialize(encoder: Encoder, value: ApiResponse<T>) =
        throw SerializationException("ApiResponse is only ever decoded")
}
