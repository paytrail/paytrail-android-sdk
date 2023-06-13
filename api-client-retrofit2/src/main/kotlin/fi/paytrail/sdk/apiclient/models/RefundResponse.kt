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

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param status
 * @param transactionId Checkout transaction ID for the refund
 * @param provider Payment method provider
 */
@Serializable
data class RefundResponse(

    @SerialName(value = "status")
    val status: RefundResponse.Status? = null,

    /* Checkout transaction ID for the refund */
    @Contextual @SerialName(value = "transactionId")
    val transactionId: java.util.UUID? = null,

    /* Payment method provider */
    @SerialName(value = "provider")
    val provider: kotlin.String? = null,

) {

    /**
     *
     *
     * Values: Ok,Pending,Fail
     */
    @Serializable
    enum class Status(val value: kotlin.String) {
        @SerialName(value = "ok")
        Ok("ok"),

        @SerialName(value = "pending")
        Pending("pending"),

        @SerialName(value = "fail")
        Fail("fail"),
    }
}
