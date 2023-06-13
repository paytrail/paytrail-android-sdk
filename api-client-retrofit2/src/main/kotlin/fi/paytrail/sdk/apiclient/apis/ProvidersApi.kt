package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.infrastructure.CollectionFormats.*
import fi.paytrail.sdk.apiclient.models.BasePaymentMethodProvider
import fi.paytrail.sdk.apiclient.models.GroupedPaymentProvidersResponse
import kotlinx.serialization.SerialName
import retrofit2.Response
import retrofit2.http.*

interface ProvidersApi {

    /**
     * enum for parameter groups
     */
    enum class Groups_getGroupedPaymentProviders(val value: kotlin.String) {
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

    /**
     * enum for parameter language
     */
    enum class Language_getGroupedPaymentProviders(val value: kotlin.String) {
        @SerialName(value = "FI")
        FI("FI"),

        @SerialName(value = "SV")
        SV("SV"),

        @SerialName(value = "EN")
        EN("EN"),
    }

    /**
     * List grouped merchant payment methods
     * Similar to the /merchants/payment-providers, but in addition of a flat list of providers, it returns payment group data containing localized names, icon URLs and grouped providers, and a localized text with a link to the terms of payment.
     * Responses:
     *  - 200: Payment methods available
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param amount Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased.  (optional)
     * @param groups Comma separated list of payment method groups to include in the reply. (optional)
     * @param language Language code of the language the terms of payment and the payment group names will be localized in. Defaults to FI if left undefined  (optional)
     * @return [GroupedPaymentProvidersResponse]
     */
    @GET("merchants/grouped-payment-providers")
    suspend fun getGroupedPaymentProviders(
        @Query("amount") amount: kotlin.Int? = null,
        @Query("groups") groups: CSVParams? = null,
        @Query("language") language: Language_getGroupedPaymentProviders? = null
    ): Response<GroupedPaymentProvidersResponse>

    /**
     * enum for parameter groups
     */
    enum class Groups_getPaymentProviders(val value: kotlin.String) {
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

    /**
     * List merchant payment methods
     * Returns the payment methods available for merchant without creating a new payment to the system. Useful for displaying payment provider icons during different phases of checkout before finally actually creating the payment request when customer decides to pay
     * Responses:
     *  - 200: Payment methods available
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param amount Optional amount in minor unit (eg. EUR cents) for the payment providers. Some providers have minimum or maximum amounts that can be purchased.  (optional)
     * @param groups Comma separated list of payment method groups to include in the reply. (optional)
     * @return [kotlin.collections.List<BasePaymentMethodProvider>]
     */
    @GET("merchants/payment-providers")
    suspend fun getPaymentProviders(
        @Query("amount") amount: kotlin.Int? = null,
        @Query("groups") groups: CSVParams? = null
    ): Response<kotlin.collections.List<BasePaymentMethodProvider>>
}
