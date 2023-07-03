package fi.paytrail.paymentsdk.model

import android.util.Log
import fi.paytrail.sdk.apiclient.models.ErrorResponse
import retrofit2.Response

data class PaytrailApiErrorResponse(
    val code: Int,
    val errorBody: String? = null,
    val errorResponse: ErrorResponse? = null,
)

fun <T> createErrorResponse(result: Response<T>): PaytrailApiErrorResponse {
    val errorBody = result.errorBody()?.string()
    val errorResponse = errorBody?.let {
        try {
            ErrorResponse.deserialize(it)
        } catch (e: IllegalArgumentException) {
            Log.w("PaymentViewModel", "Failed deserializing error body: $errorBody")
            null
        }
    }
    return PaytrailApiErrorResponse(
        code = result.code(),
        errorBody = errorBody,
        errorResponse = errorResponse,
    )
}
