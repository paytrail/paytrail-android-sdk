package fi.paytrail.sdk.apiclient.apis

import fi.paytrail.sdk.apiclient.models.AddCardFormRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse
import fi.paytrail.sdk.apiclient.models.TokenizationRequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface TokenPaymentsApi {
    /**
     * Request a redirect to card addition form
     * Request a redirect to card addition form. This will redirect user to the card addition form, then back to Checkout servers, and finally to merchant&#39;s redirect url.
     * Responses:
     *  - 302: Redirect to card addition form (or checkout-redirect-cancel-url)
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 403: Tokenization not allowed for merchant
     *
     * @param addCardFormRequest Add card payload
     * @return [Unit]
     */
    @POST("tokenization/addcard-form")
    suspend fun addCardForm(@Body addCardFormRequest: AddCardFormRequest): Response<Unit>

    /**
     * Request a card token for given tokenization id
     * Use checkout-tokenization-id received from /tokenization/addcard-form redirect to request a token which can be used for payments.
     * Responses:
     *  - 200: Tokenization request response
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 403: Credit cards and/or AMEX not enabled for merchant
     *
     * @param checkoutTokenizationId Tokenization ID.
     * @param checkoutTokenizationIdHeader Must be same as [checkoutTokenizationId]; default value is copied from this parameter.
     *
     * @return [TokenizationRequestResponse]
     */
    @POST("tokenization/{checkout-tokenization-id}")
    suspend fun requestTokenForTokenizationId(
        @Path("checkout-tokenization-id") checkoutTokenizationId: String,
        @Header("checkout-tokenization-id") checkoutTokenizationIdHeader: String = checkoutTokenizationId,
    ): Response<TokenizationRequestResponse>

    /**
     * Request customer initiated transaction authorization hold on token
     * Request customer initiated transaction authorization hold on token. CIT authorization holds can sometimes require further 3DS authentication step-up. See detailed documentation at https://checkoutfinland.github.io/psp-api/#/?id&#x3D;customer-initiated-transactions-cit
     * Responses:
     *  - 201: CIT authorization hold request created successfully.
     *  - 403: CIT authorization hold requires 3DS authentication step up. Please redirect user to given URL.
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param tokenPaymentRequest CIT authorization hold payload
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/token/cit/authorization-hold")
    suspend fun tokenCitAuthorizationHold(
        @Body tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse>

    /**
     * Request customer initiated transaction charge on token
     * Request customer initiated transaction charge on token. CIT charges can sometimes require further 3DS authentication step-up. See detailed documentation at https://checkoutfinland.github.io/psp-api/#/?id&#x3D;customer-initiated-transactions-cit
     * Responses:
     *  - 201: CIT charge request created successfully.
     *  - 403: CIT charge requires 3DS authentication step up. Please redirect user to given URL.
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param tokenPaymentRequest CIT Charge payload
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/token/cit/charge")
    suspend fun tokenCitCharge(
        @Body tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse>

    /**
     * Request committing (charging) of previously created authorization hold on token
     * Request committing of previously created authorization hold. The final amount committed can either equal or be less than the authorization hold. The committed amount may not exceed the authorization hold. The final items may differ from the ones used when creating the authorization hold.
     * Responses:
     *  - 201: CIT authorization hold committed successfully.
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param tokenPaymentRequest CIT commit payload
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/{transactionId}/token/commit")
    suspend fun tokenCommit(
        @Path("transactionId") transactionId: UUID,
        @Header("checkout-transaction-id") checkoutTransactionId: UUID? = transactionId,
        @Body tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse>

    /**
     * Request merchant initiated transaction authorization hold on token
     * Request merchant initiated transaction authorization hold on token. This method should be used when creating an authorization hold on the customer&#39;s card in a context, where the customer is not actively participating in the transaction.
     * Responses:
     *  - 201: MIT authorization hold request created successfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param tokenPaymentRequest MIT Charge payload
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/token/mit/authorization-hold")
    suspend fun tokenMitAuthorizationHold(
        @Body tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse>

    /**
     * Request merchant initiated transaction charge on token
     * Request merchant initiated transaction charge on token. This method should be used when charging the customer&#39;s card in a context, where the customer is not actively participating in the transaction.
     * Responses:
     *  - 201: MIT charge request created successfully
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @param tokenPaymentRequest MIT Charge payload
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/token/mit/charge")
    suspend fun tokenMitCharge(
        @Body tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse>

    /**
     * Revert (removal) of previously created authorization hold on token
     * Request committing of previously created authorization hold. A successful revert will remove the authorization hold from the payer&#39;s bank account.
     * Responses:
     *  - 200: Authorization hold reverted successfully.
     *  - 400: Request did not pass input validation
     *  - 401: Unauthorized
     *  - 0: Unexpected error
     *
     * @return [TokenMITPaymentResponse]
     */
    @POST("payments/{transactionId}/token/revert")
    suspend fun tokenRevert(
        @Path("transactionId") transactionId: UUID,
        @Header("checkout-transaction-id") checkoutTransactionId: UUID? = transactionId,
    ): Response<TokenPaymentResponse>
}
