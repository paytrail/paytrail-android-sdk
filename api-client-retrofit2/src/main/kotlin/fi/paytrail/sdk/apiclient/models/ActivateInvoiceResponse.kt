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

/**
 *
 *
 * @param status
 * @param message Response details
 */
@Serializable
data class ActivateInvoiceResponse(

    @SerialName(value = "status")
    val status: ActivateInvoiceResponse.Status? = null,

    /* Response details */
    @SerialName(value = "message")
    val message: kotlin.String? = null,

) {

    /**
     *
     *
     * Values: Ok,Error
     */
    @Serializable
    enum class Status(val value: kotlin.String) {
        @SerialName(value = "ok")
        Ok("ok"),

        @SerialName(value = "error")
        Error("error"),
    }
}
