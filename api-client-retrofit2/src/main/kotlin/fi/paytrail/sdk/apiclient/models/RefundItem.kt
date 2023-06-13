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
 * @param amount Amount to refund
 * @param stamp Original item stamp
 * @param refundStamp Merchant specific unique stamp for the refund
 * @param refundReference Merchant reference for the refund
 */
@Serializable
data class RefundItem(

    /* Amount to refund */
    @SerialName(value = "amount")
    val amount: kotlin.Long,

    /* Original item stamp */
    @SerialName(value = "stamp")
    val stamp: kotlin.String,

    /* Merchant specific unique stamp for the refund */
    @SerialName(value = "refundStamp")
    val refundStamp: kotlin.String? = null,

    /* Merchant reference for the refund */
    @SerialName(value = "refundReference")
    val refundReference: kotlin.String? = null,

)
