/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport",
)

package fi.paytrail.sdk.apiclient.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Error response from Paytrail APIs.
 *
 * @param status
 * @param message Error description
 * @param meta Possible detailed error descriptions
 */
@Serializable
data class ErrorResponse(

    @SerialName(value = "status")
    val status: Status,

    /* Error description */
    @SerialName(value = "message")
    val message: kotlin.String,

    /* Possible detailed error descriptions */
    @SerialName(value = "meta")
    val meta: kotlin.collections.List<kotlin.String>? = null,

    /**
     * Acquirer response code or empty.
     * Relevant for tokenized payments.
     */
    @SerialName(value = "acquirerResponseCode")
    val acquirerResponseCode: String? = null,

    /**
     * Meaningful description of the acquirer response code or empty.
     * Relevant for tokenized payments.
     */
    @SerialName(value = "acquirerResponseCodeDescription")
    val acquirerResponseCodeDescription: String? = null,

) {
    /**
     *
     *
     * Values: Error
     */
    @Serializable
    enum class Status(val value: kotlin.String) {
        @SerialName(value = "error")
        Error("error"),
    }

    companion object {
        fun deserialize(json: String): ErrorResponse = Json.decodeFromString(json)
    }
}
