package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.infrastructure.CollectionFormats.CSVParams
import fi.paytrail.sdk.apiclient.models.ActivateInvoiceResponse
import fi.paytrail.sdk.apiclient.models.BasePaymentMethodProvider
import fi.paytrail.sdk.apiclient.models.GroupedPaymentProvidersResponse
import fi.paytrail.sdk.apiclient.models.Groups
import fi.paytrail.sdk.apiclient.models.Language
import fi.paytrail.sdk.apiclient.models.PayAndAddCardResponse
import fi.paytrail.sdk.apiclient.models.Payment
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.PaymentRequestResponse
import fi.paytrail.sdk.apiclient.models.Refund
import fi.paytrail.sdk.apiclient.models.RefundResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface PaymentsApi {

    /**
     * Activate invoice
     * Manually activate invoice by transaction ID. Can only be used if payment was paid with Collector and is in pending status.
     * Responses:
     *  - 200: Invoice activated succesfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 0: Unexpected error
     *
     * @param transactionId Transaction ID of payment to activate invoice
     * @param checkoutTransactionId The same transaction ID as in route (optional)
     * @return [ActivateInvoiceResponse]
     */
    @POST("payments/{transactionId}/activate-invoice")
    suspend fun activateInvoiceByTransactionId(
        @Path("transactionId") transactionId: UUID,
        @Header("checkout-transaction-id") checkoutTransactionId: UUID? = transactionId,
    ): Response<ActivateInvoiceResponse>

    /**
     * Create a new open payment
     * Create a new open payment, returns a list of available payment methods.
     * Responses:
     *  - 201: Payment request created successfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param paymentRequest Payment body payload
     * @return [PaymentRequestResponse]
     */
    @POST("payments")
    suspend fun createPayment(@Body paymentRequest: PaymentRequest): Response<PaymentRequestResponse>

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
    @JvmSuppressWildcards
    suspend fun getGroupedPaymentProviders(
        @Query("amount") amount: Long? = null,
        @Query("groups") groups: List<Groups>? = null,
        @Query("language") language: Language? = null,
    ): Response<GroupedPaymentProvidersResponse>

    /**
     * Get a payment by Checkout transaction ID
     * Get a single payment
     * Responses:
     *  - 200: Payment response
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 0: Unexpected error
     *
     * @param transactionId Transaction ID of payment to fetch
     * @param checkoutTransactionId The same transaction ID as in route (optional)
     * @return [Payment]
     */
    @GET("payments/{transactionId}")
    suspend fun getPaymentByTransactionId(
        @Path("transactionId") transactionId: UUID,
        @Header("checkout-transaction-id") checkoutTransactionId: UUID? = transactionId,
    ): Response<Payment>

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

    /**
     * Refund a payment
     * Refund a payment by transaction ID. Refund operation is asynchronous. Refund request is validated, and if the refund can be done a 201 is returned.
     * Responses:
     *  - 201: Refund created succesfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 404: The specified resource was not found
     *  - 422: Payment method provider does not support refunds
     *  - 0: Unexpected error
     *
     * @param transactionId Transaction ID of payment to refund
     * @param refund Refund payload
     * @param checkoutTransactionId The same transaction ID as in route (optional)
     * @return [RefundResponse]
     */
    @POST("payments/{transactionId}/refund")
    suspend fun refundPaymentByTransactionId(
        @Path("transactionId") transactionId: UUID,
        @Body refund: Refund,
        @Header("checkout-transaction-id") checkoutTransactionId: UUID? = transactionId,
    ): Response<RefundResponse>

    /**
     * This method is an alternative way to add (tokenize) a card, which combines a payment
     * and adding a new card to allow getting the card token after a successful payment with
     * a single request.
     *
     * If the flow is successfully completed, the given success-callback URL will be called
     * with an additional parameter: checkout-card-token, which is also included in the
     * HMAC-calculation of the signature-parameter. This token can be saved by the merchant
     * and additional payments can be charged on the token. The token is not included in the
     * redirect URL parameters, as we don't want the user to be able to see the token.
     *
     * If the flow fails due to issues with the card itself (insufficient funds, fraud etc.),
     * the given cancel-callback URL will be called with additional parameters
     * `checkout-acquirer-response-code` and `checkout-acquirer-response-code-description`
     * for troubleshooting the possible cause.
     *
     * Note that the callback is not visible to application itself. This is a backend-to-backend
     * callback, and the merchant application must retrieve the token from merchant backend
     * to use it for tokenized payments.
     *
     * Responses:
     *  - 200: Payment request created successfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 403: Tokenization not allowed for merchant
     *
     * @param addCardFormRequest Add card payload
     * @return [PayAndAddCardResponse]
     */
    @POST("tokenization/pay-and-add-card")
    suspend fun payAndAddCard(@Body paymentRequest: PaymentRequest): Response<PayAndAddCardResponse>
}
