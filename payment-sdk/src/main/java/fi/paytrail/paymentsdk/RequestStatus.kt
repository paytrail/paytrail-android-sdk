package fi.paytrail.paymentsdk

import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse

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
