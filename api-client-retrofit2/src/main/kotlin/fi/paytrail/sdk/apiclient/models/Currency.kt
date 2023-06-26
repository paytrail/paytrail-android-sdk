package fi.paytrail.sdk.apiclient.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
