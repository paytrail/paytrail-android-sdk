package fi.paytrail.paymentsdk

import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.model.createErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
/**
 * Represents the status of a request made to the backend.
 *
 * This data class helps in wrapping the result of an API call or any other request,
 * providing a unified way to represent the success, loading, or error state.
 *
 * @param T The type of the data to be returned in case of a successful request.
 * @param status The current status of the request (e.g., LOADING, SUCCESS, ERROR).
 * @param value The actual data in case the request was successful.
 * @param error The error response from the API if any.
 * @param exception Any exception that might have occurred during the request.
 */
data class RequestStatus<T> constructor(
    val status: Status,
    val value: T? = null,
    val error: PaytrailApiErrorResponse? = null,
    val exception: Exception? = null,
) {

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR,
    }

    companion object {
        fun <V> loading() = RequestStatus<V>(status = Status.LOADING)

        fun <V> success(value: V? = null) = RequestStatus(status = Status.SUCCESS, value = value)

        fun <V> error(
            error: PaytrailApiErrorResponse? = null,
            exception: Exception? = null,
        ) = RequestStatus<V>(
            status = Status.ERROR,
            error = error,
            exception = exception,
        )
    }

    val isLoading = status == Status.LOADING
    val isSuccess = status == Status.SUCCESS
    val isError = status == Status.ERROR

    fun <V> map(func: (T?) -> V?): RequestStatus<V> {
        return RequestStatus(
            status = status,
            value = func(value),
            error = error,
            exception = exception,
        )
    }
}

/**
 * Creates a new [Flow] to execute [func] in, and emits the result as [RequestStatus].
 * Emits [RequestStatus.loading] before making the request.
 */
fun <T> flowApiRequest(func: suspend () -> Response<T>): Flow<RequestStatus<T>> = flow {
    emit(RequestStatus.loading())
    emit(apiRequest(func))
}

/**
 * Make a retrofit API call, and convert the result to [RequestStatus].
 */
suspend fun <T> apiRequest(func: suspend () -> Response<T>): RequestStatus<T> = try {
    val response = func()
    if (response.isSuccessful) {
        RequestStatus.success(response.body())
    } else {
        RequestStatus.error(error = createErrorResponse(response))
    }
} catch (e: Exception) {
    RequestStatus.error(exception = e)
}
