package fi.paytrail.paymentsdk.tokenization.model

import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.tokenization.AddCardRedirect
/**
 * Represents the result after attempting to add a card.
 *
 * This data class encapsulates the outcome of the card addition process, whether it's successful,
 * a failure, or encounters an error. It provides comprehensive information about the process,
 * including the redirected URL, potential API errors, or exceptions.
 *
 * @param result Enum indicating the result status (e.g., SUCCESS, FAILURE, ERROR).
 * @property redirect Detailed information about the redirected URL post card addition. This
 * include valuable data like the 'checkout-tokenization-id' which can be stored locally for subsequent operations.
 * @param error Information about any API errors that might have occurred.
 * @param exception Details of any exception that might have occurred during the card addition.
 */
data class AddCardResult(
    val result: Result,
    val redirect: AddCardRedirect? = null,
    val error: PaytrailApiErrorResponse? = null,
    val exception: Exception? = null,
) {
    /**
     * Enum class representing the possible results of the card addition process.
     */
    enum class Result {
        SUCCESS,
        FAILURE,
        ERROR,
    }
}
