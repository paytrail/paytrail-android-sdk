package fi.paytrail.paymentsdk.model

import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse

// TODO: Include transaction id in the state when it is known
// TODO: Should other payment request info be included in the state?
data class PaytrailPaymentState internal constructor(
    val state: State,
    val finalRedirectRequest: PaytrailPaymentRedirect? = null,
    val tokenPaymentResponse: TokenPaymentResponse? = null,
    val apiErrorResponse: PaytrailApiErrorResponse? = null,
    val exception: Exception? = null,
) {
    enum class State {
        LOADING_PAYMENT_METHODS,
        SHOW_PAYMENT_METHODS,

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
