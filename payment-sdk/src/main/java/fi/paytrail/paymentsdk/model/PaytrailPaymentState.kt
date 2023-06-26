package fi.paytrail.paymentsdk.model

// TODO: Include transaction id in the state when it is known?
// TODO: Should other paymebnt request info be included in the state?
data class PaytrailPaymentState internal constructor(
    val state: State,
    val redirectRequest: PaytrailPaymentRedirect? = null,
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
         * Payment was completed successfully. PaytrailPaymentState contains [redirectRequest]
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
