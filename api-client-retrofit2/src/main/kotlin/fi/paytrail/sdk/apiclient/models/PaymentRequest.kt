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
 * Payment request payload
 *
 * @param stamp Merchant specific unique stamp
 * @param reference Merchant reference for the payment
 * @param amount Total amount of the payment (sum of items), VAT should be included in amount unless `usePricesWithoutVat` is set to true
 * @param currency
 * @param language Alpha-2 language code for the payment process
 * @param items
 * @param customer
 * @param redirectUrls
 * @param orderId Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead.
 * @param deliveryAddress
 * @param invoicingAddress
 * @param manualInvoiceActivation If paid with invoice payment method, the invoice will not be activated automatically immediately. Currently only supported with Collector.
 * @param callbackUrls
 * @param callbackDelay Callback delay in seconds. If callback URLs and delay are provided, callbacks will be called after the delay.
 * @param groups Optionally return only payment methods for selected groups
 * @param usePricesWithoutVat If true, `amount` and `items.unitPrice` should be sent to API without VAT, and final VAT-included prices are calculated by Paytrail's system (with prices rounded to closest cent). Also, when true, items must be included.
 */
@Serializable
data class PaymentRequest(

    /* Merchant specific unique stamp */
    @SerialName(value = "stamp")
    val stamp: kotlin.String,

    /* Merchant reference for the payment */
    @SerialName(value = "reference")
    val reference: kotlin.String,

    /* Total amount of the payment (sum of items), VAT should be included in amount unless `usePricesWithoutVat` is set to true */
    @SerialName(value = "amount")
    val amount: kotlin.Long,

    @SerialName(value = "currency")
    val currency: PaymentRequest.Currency,

    /* Alpha-2 language code for the payment process */
    @SerialName(value = "language")
    val language: PaymentRequest.Language,

    @SerialName(value = "items")
    val items: kotlin.collections.List<Item>? = null,

    @SerialName(value = "customer")
    val customer: Customer,

    @SerialName(value = "redirectUrls")
    val redirectUrls: Callbacks,

    /* Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead. */
    @SerialName(value = "orderId")
    val orderId: kotlin.String? = null,

    @SerialName(value = "deliveryAddress")
    val deliveryAddress: Address? = null,

    @SerialName(value = "invoicingAddress")
    val invoicingAddress: Address? = null,

    /* If paid with invoice payment method, the invoice will not be activated automatically immediately. Currently only supported with Collector. */
    @SerialName(value = "manualInvoiceActivation")
    val manualInvoiceActivation: kotlin.Boolean? = null,

    @SerialName(value = "callbackUrls")
    val callbackUrls: Callbacks? = null,

    /* Callback delay in seconds. If callback URLs and delay are provided, callbacks will be called after the delay. */
    @SerialName(value = "callbackDelay")
    val callbackDelay: kotlin.Int? = null,

    /* Optionally return only payment methods for selected groups */
    @SerialName(value = "groups")
    val groups: kotlin.collections.List<PaymentRequest.Groups>? = null,

    /* If true, `amount` and `items.unitPrice` should be sent to API without VAT, and final VAT-included prices are calculated by Paytrail's system (with prices rounded to closest cent). Also, when true, items must be included. */
    @SerialName(value = "usePricesWithoutVat")
    val usePricesWithoutVat: kotlin.Boolean? = null,

) {

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

    /**
     * Alpha-2 language code for the payment process
     *
     * Values: FI,SV,EN
     */
    @Serializable
    enum class Language(val value: kotlin.String) {
        @SerialName(value = "FI")
        FI("FI"),

        @SerialName(value = "SV")
        SV("SV"),

        @SerialName(value = "EN")
        EN("EN"),
    }

    /**
     * Optionally return only payment methods for selected groups
     *
     * Values: Mobile,Bank,Creditcard,Credit,Other
     */
    @Serializable
    enum class Groups(val value: kotlin.String) {
        @SerialName(value = "mobile")
        Mobile("mobile"),

        @SerialName(value = "bank")
        Bank("bank"),

        @SerialName(value = "creditcard")
        Creditcard("creditcard"),

        @SerialName(value = "credit")
        Credit("credit"),

        @SerialName(value = "other")
        Other("other"),
    }
}
