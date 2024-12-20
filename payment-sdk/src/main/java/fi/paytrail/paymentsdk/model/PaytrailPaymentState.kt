package fi.paytrail.paymentsdk.model

import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse
import java.time.LocalDateTime
import java.util.UUID
/**
 * Represents the state of the payment process in the Paytrail system.
 * This data class provides insights and updates regarding the various stages and outcomes
 * of the payment process. It can be used by the consumer of the SDK to handle UI changes,
 * error handling, and other relevant operations based on the payment state.
 *
 * @param state Current state of the payment process.
 * @param timestamp Timestamp marking the time of state initialization.
 * @param finalRedirectRequest Contains parameters derived from the checkout redirect link.
 * @param tokenPaymentResponse Response received when a tokenized payment is initiated.
 * @param apiErrorResponse Represents any error response when interfacing with the Paytrail API.
 * @param exception Any exception that might emerge during the payment operations.
 */
data class PaytrailPaymentState internal constructor(
    val state: State,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val finalRedirectRequest: PaytrailPaymentRedirect? = null,
    val tokenPaymentResponse: TokenPaymentResponse? = null,
    val apiErrorResponse: PaytrailApiErrorResponse? = null,
    val exception: Exception? = null,
) {
    val transactionId: UUID?
        get() = tokenPaymentResponse?.transactionId
            ?: finalRedirectRequest?.transactionId?.let { UUID.fromString(it) }

    enum class State {
        LOADING_PAYMENT_PROVIDERS,

        /**
         * This state will trigger PaymentProviders to show available payment providers.
         */
        SHOW_PAYMENT_PROVIDERS,

        /**
         * Payment with a payment method is ongoing.
         */
        PAYMENT_IN_PROGRESS,

        /**
         * Payment was completed successfully. PaytrailPaymentState contains [finalRedirectRequest]
         * with the data relevant to the payment.
         */
        PAYMENT_OK,

        /** Payment provider reported the payment failed. */
        PAYMENT_FAIL,

        /** Payment was canceled by user. */
        PAYMENT_CANCELED,

        /**
         * Payment failed due to a local error, for example connection failure or backend APis
         * returned an error result.
         */
        PAYMENT_ERROR,
    }
}
