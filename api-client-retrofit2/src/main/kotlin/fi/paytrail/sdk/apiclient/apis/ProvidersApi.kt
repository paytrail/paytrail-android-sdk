package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.infrastructure.CollectionFormats.CSVParams
import fi.paytrail.sdk.apiclient.models.BasePaymentMethodProvider
import fi.paytrail.sdk.apiclient.models.GroupedPaymentProvidersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProvidersApi {

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
        @Query("amount") amount: Int? = null,
        @Query("groups") groups: CSVParams? = null,
        @Query("language") language: Language? = null,
    ): Response<GroupedPaymentProvidersResponse>

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
        @Query("amount") amount: Int? = null,
        @Query("groups") groups: CSVParams? = null,
    ): Response<List<BasePaymentMethodProvider>>
}
