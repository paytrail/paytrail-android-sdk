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
 * Settlement ID
 *
 * @param id ID of a settlement
 * @param createdAt When the settlement was formed
 * @param settledAt When the settlement was paid
 */
@Serializable
data class SettlementIdItem(

    /* ID of a settlement */
    @SerialName(value = "id")
    val id: kotlin.Long,

    /* When the settlement was formed */
    @SerialName(value = "createdAt")
    val createdAt: kotlin.String,

    /* When the settlement was paid */
    @SerialName(value = "settledAt")
    val settledAt: kotlin.String,

)
