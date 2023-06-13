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
 * @param unitPrice Unit price of an item in currency minor unit, eg. EUR cents. VAT should be included in amount unless `usePricesWithoutVat` is set to true.
 * @param units Number of units
 * @param vatPercentage Item VAT percentage
 * @param productCode Merchant specific product code
 * @param deliveryDate Estimated delivery date
 * @param description Merchant specific product description
 * @param category Item product category
 * @param merchant Submerchant ID. Required for shop-in-shop payments, leave out from normal payments.
 * @param stamp Submerchant specific unique stamp. Required for shop-in-shop payments, leave out from normal payments.
 * @param reference Submerchant reference for the item. Required for shop-in-shop payments, leave out from normal payments.
 * @param orderId Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead.
 * @param commission
 */
@Serializable
data class Item(

    /* Unit price of an item in currency minor unit, eg. EUR cents. VAT should be included in amount unless `usePricesWithoutVat` is set to true. */
    @SerialName(value = "unitPrice")
    val unitPrice: kotlin.Long,

    /* Number of units */
    @SerialName(value = "units")
    val units: kotlin.Long,

    /* Item VAT percentage */
    @SerialName(value = "vatPercentage")
    val vatPercentage: kotlin.Long,

    /* Merchant specific product code */
    @SerialName(value = "productCode")
    val productCode: kotlin.String,

    /* Estimated delivery date */
    @Contextual @SerialName(value = "deliveryDate")
    val deliveryDate: java.time.LocalDate? = null,

    /* Merchant specific product description */
    @SerialName(value = "description")
    val description: kotlin.String? = null,

    /* Item product category */
    @SerialName(value = "category")
    val category: kotlin.String? = null,

    /* Submerchant ID. Required for shop-in-shop payments, leave out from normal payments. */
    @SerialName(value = "merchant")
    val merchant: kotlin.String? = null,

    /* Submerchant specific unique stamp. Required for shop-in-shop payments, leave out from normal payments. */
    @SerialName(value = "stamp")
    val stamp: kotlin.String? = null,

    /* Submerchant reference for the item. Required for shop-in-shop payments, leave out from normal payments. */
    @SerialName(value = "reference")
    val reference: kotlin.String? = null,

    /* Order ID. Used for eg. Collector payments order ID. If not given, merchant reference is used instead. */
    @SerialName(value = "orderId")
    val orderId: kotlin.String? = null,

    @SerialName(value = "commission")
    val commission: ItemCommission? = null,

)
