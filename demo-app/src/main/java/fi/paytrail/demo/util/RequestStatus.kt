package fi.paytrail.demo.util

import fi.paytrail.sdk.apiclient.models.ErrorResponse

// TODO: Move this into SDK?
data class RequestStatus<T> constructor(
    val status: Status,
    val value: T? = null,
    val error: ErrorResponse? = null,
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
            error: ErrorResponse? = null,
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
