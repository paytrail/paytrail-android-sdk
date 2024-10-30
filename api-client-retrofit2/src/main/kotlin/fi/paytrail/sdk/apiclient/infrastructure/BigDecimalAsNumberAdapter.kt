package fi.paytrail.sdk.apiclient.infrastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import java.math.BigDecimal

object BigDecimalAsNumberAdapter : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigDecimalAsNumber", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        when (encoder) {
            is JsonEncoder -> {
                encoder.encodeJsonElement(JsonPrimitive(value))
            }
            else -> {
                // Fallback for non-JSON encoders, encode as string
                encoder.encodeString(value.toPlainString())
            }
        }
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return when (decoder) {
            is JsonDecoder -> {
                val jsonElement = decoder.decodeJsonElement()
                if (jsonElement is JsonPrimitive && !jsonElement.isString) {
                    return BigDecimal(jsonElement.content)
                } else {
                    throw SerializationException("Expected JSON number for BigDecimal")
                }
            }
            else -> {
                // Fallback for non-JSON decoders
                BigDecimal(decoder.decodeString())
            }
        }
    }
}
