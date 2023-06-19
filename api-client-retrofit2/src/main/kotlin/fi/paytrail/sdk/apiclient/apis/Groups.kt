package fi.paytrail.sdk.apiclient.apis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * enum for parameter groups
 */
@Serializable
enum class Groups(val value: String) {
    @SerialName(value = "mobile")
    Mobile("mobile"),

    @SerialName(value = "bank")
    Bank("bank"),

    @SerialName(value = "creditcard")
    CreditCard("creditcard"),

    @SerialName(value = "credit")
    Credit("credit"),

    @SerialName(value = "other")
    Other("other"),
}
