@file:OptIn(ExperimentalCoroutinesApi::class)

package fi.paytrail.paymentsdk

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.createErrorResponse
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PayAndAddCardResponse
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

class PayAndAddCardViewModel(
    val paymentRequest: PaymentRequest,
    apiClient: PaytrailApiClient,
) : ViewModel() {

    private val api = apiClient.createService(PaymentsApi::class.java)

    private val payAndAddCardRequestStatus: Flow<RequestStatus<PayAndAddCardResponse>> = flow {
        emit(RequestStatus.loading())
        try {
            val response = api.payAndAddCard(paymentRequest = paymentRequest)
            if (response.isSuccessful) {
                emit(RequestStatus.success(response.body()))
            } else {
                val errorResponse = createErrorResponse(response)
                emit(RequestStatus.error(errorResponse))
            }
        } catch (e: Exception) {
            emit(RequestStatus.error(exception = e))
        }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1,
    )

    private val payAndAddCardResponse: Flow<PayAndAddCardResponse?> = payAndAddCardRequestStatus
        .flatMapLatest {
            flow {
                if (it.isSuccess) emit(it.value)
            }
        }

    val paymentRedirectUrl: Flow<String> = payAndAddCardResponse.map {
        it?.redirectUrl ?: ""
    }

    private val finalRedirect = MutableStateFlow<PaytrailPaymentRedirect?>(null)
    private val webviewException = MutableStateFlow<Exception?>(null)

    private val apiErrorResponse: Flow<PaytrailApiErrorResponse?> =
        payAndAddCardRequestStatus.flatMapLatest {
            flow {
                if (it.isError && it.error != null) {
                    emit(it.error)
                }
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private val apiException: Flow<Exception?> =
        payAndAddCardRequestStatus.flatMapLatest {
            flow {
                if (it.isError && it.exception != null) {
                    emit(it.exception)
                }
            }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private val paymentException = combine(apiException, webviewException) { a, b -> a ?: b }

    val paymentState: Flow<PaytrailPaymentState> = combine(
        flow = finalRedirect.onStart { emit(null) },
        flow2 = apiErrorResponse.onStart { emit(null) },
        flow3 = paymentException.onStart { emit(null) },
        transform = {
                redirect: PaytrailPaymentRedirect?,
                errorResponse: PaytrailApiErrorResponse?,
                exception: Exception?,
            ->

            when {
                errorResponse != null || exception != null || redirect?.status == PaytrailPaymentRedirect.Status.Fail -> PaytrailPaymentState(
                    state = PaytrailPaymentState.State.PAYMENT_FAIL,
                    finalRedirectRequest = redirect,
                    apiErrorResponse = errorResponse,
                    exception = exception,
                )

                redirect?.status == PaytrailPaymentRedirect.Status.Ok -> PaytrailPaymentState(
                    state = PaytrailPaymentState.State.PAYMENT_OK,
                    finalRedirectRequest = redirect,
                )

                else -> PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS)
            }
        },
    ).shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1,
    )

    fun finalRedirectReceived(uri: Uri) {
        finalRedirect.value = PaytrailPaymentRedirect(uri)
    }

    fun webviewErrorReceived(exception: Exception) {
        webviewException.value = exception
    }
}
