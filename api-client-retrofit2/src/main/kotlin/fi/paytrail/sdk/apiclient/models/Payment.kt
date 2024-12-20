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
 * @param status Transaction status
 * @param amount
 * @param currency
 * @param stamp
 * @param reference
 * @param createdAt
 * @param transactionId Transaction ID
 * @param href If transaction is in status 'new', link to the hosted payment gateway
 * @param provider If processed, the name of the payment method provider
 * @param filingCode If paid, the filing code issued by the payment method provider if any
 * @param paidAt Timestamp when the transaction was paid
 */
@Serializable
data class Payment(

    /* Transaction status */
    @SerialName(value = "status")
    val status: Payment.Status,

    @SerialName(value = "amount")
    val amount: kotlin.Long,

    @SerialName(value = "currency")
    val currency: Payment.Currency,

    @SerialName(value = "stamp")
    val stamp: kotlin.String,

    @SerialName(value = "reference")
    val reference: kotlin.String,

    @SerialName(value = "createdAt")
    val createdAt: kotlin.String,

    /* Transaction ID */
    @Contextual @SerialName(value = "transactionId")
    val transactionId: java.util.UUID? = null,

    /* If transaction is in status 'new', link to the hosted payment gateway */
    @SerialName(value = "href")
    val href: kotlin.String? = null,

    /* If processed, the name of the payment method provider */
    @SerialName(value = "provider")
    val provider: kotlin.String? = null,

    /* If paid, the filing code issued by the payment method provider if any */
    @SerialName(value = "filingCode")
    val filingCode: kotlin.String? = null,

    /* Timestamp when the transaction was paid */
    @SerialName(value = "paidAt")
    val paidAt: kotlin.String? = null,

) {

    /**
     * Transaction status
     *
     * Values: New,Ok,Fail,Pending,Delayed,AuthorizationHold
     */
    @Serializable
    enum class Status(val value: kotlin.String) {
        @SerialName(value = "new")
        New("new"),

        @SerialName(value = "ok")
        Ok("ok"),

        @SerialName(value = "fail")
        Fail("fail"),

        @SerialName(value = "pending")
        Pending("pending"),

        @SerialName(value = "delayed")
        Delayed("delayed"),

        @SerialName(value = "authorization-hold")
        AuthorizationHold("authorization-hold"),
    }

    /**
     *
     *
     * Values: EUR
     */
    @Serializable
    enum class Currency(val value: kotlin.String) {
        @SerialName(value = "EUR")
        EUR("EUR"),
    }
}
