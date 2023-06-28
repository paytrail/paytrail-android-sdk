package fi.paytrail.paymentsdk.tokenization.model

import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.tokenization.AddCardRedirect

data class AddCardResult(
    val result: Result,
    val redirect: AddCardRedirect? = null,
    val error: PaytrailApiErrorResponse? = null,
    val exception: Exception? = null,
) {
    enum class Result {
        SUCCESS,
        FAILURE,
        ERROR,
    }
}
