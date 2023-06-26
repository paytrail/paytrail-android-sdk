package fi.paytrail.sdk.apiclient.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * enum for parameter language
 */
@Serializable
enum class Language(val value: String) {
    @SerialName(value = "FI")
    FI("FI"),

    @SerialName(value = "SV")
    SV("SV"),

    @SerialName(value = "EN")
    EN("EN"),
}
