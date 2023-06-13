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
 * @param networkAddress Customer IP address
 * @param countryCode Customer country code
 */
@Serializable
data class TokenCustomerDetails(

    /* Customer IP address */
    @SerialName(value = "network_address")
    val networkAddress: kotlin.String,

    /* Customer country code */
    @SerialName(value = "country_code")
    val countryCode: kotlin.String,

)
