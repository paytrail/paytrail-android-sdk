package fi.paytrail.paymentsdk.tokenization

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.createErrorResponse
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import retrofit2.Response

class PayWithTokenViewModel(
    private val tokenizationId: String?,
    private val token: String?,
    val paymentRequest: PaymentRequest,
    private val paymentType: TokenPaymentType,
    private val chargeType: TokenPaymentChargeType,
    private val apiClient: PaytrailApiClient,
) : ViewModel() {

    private val api by lazy { apiClient.createService(TokenPaymentsApi::class.java) }

    val paymentToken: Flow<RequestStatus<String>> = flow {
        if (token != null) {
            emit(RequestStatus.success(token))
        } else if (tokenizationId != null) {
            emit(RequestStatus.loading())
            emit(
                try {
                    val response = api.requestTokenForTokenizationId(tokenizationId)
                    if (response.isSuccessful) {
                        val tokenizationRequestResponse = requireNotNull(response.body())
                        RequestStatus.success(tokenizationRequestResponse.token)
                    } else {
                        val errorResponse = createErrorResponse(response)
                        RequestStatus.error(errorResponse)
                    }
                } catch (e: Exception) {
                    RequestStatus.error(exception = e)
                },
            )
        } else {
            throw IllegalArgumentException("Either token or tokenizationId must be provided")
        }
    }.shared()

    private val paymentResponse: Flow<RequestStatus<out TokenPaymentResponse>> = paymentToken.map {
        when {
            it.isSuccess -> {
                val paymentToken = requireNotNull(it.value)
                val tokenPaymentRequest = paymentRequest.asTokenizedPaymentRequest(paymentToken)
                val response = callTokenPaymentApi(tokenPaymentRequest)
                try {
                    when {
                        response.isSuccessful -> {
                            RequestStatus.success(requireNotNull(response.body()))
                        }

                        response.code() == 403 -> {
                            // 403 indicates a redirect to 3DSecure verification. It is not an error
                            // for token payment APIs. Instead app needs to show the 3DSecure flow
                            // in a webview. It is passed through as a success, with
                            RequestStatus.success(
                                TokenPaymentResponse.deserialize(
                                    response.errorBody()?.string() ?: "",
                                ),
                            )
                        }

                        else -> {
                            val errorResponse = createErrorResponse(response)
                            RequestStatus.error(errorResponse)
                        }
                    }
                } catch (e: Exception) {
                    RequestStatus.error(exception = e)
                }
            }

            // loading & error statuses from paymentToken flow can be passed through with
            // null value.
            else -> it.map { null }
        }
    }.shared()

    val payment3DSRedirectUrl = paymentResponse.map {
        it.value?.threeDSecureUrl
    }.shared()

    private val tokenPaymentResponse: Flow<TokenPaymentResponse?> =
        paymentResponse.flatMapLatest {
            flow {
                if (it.isSuccess && it.value?.threeDSecureUrl.isNullOrEmpty()) {
                    emit(requireNotNull(it.value))
                }
            }
        }.shared()

    private val apiErrorResponse: Flow<PaytrailApiErrorResponse?> =
        paymentResponse.flatMapLatest {
            flow {
                if (it.isError && it.error != null) {
                    emit(it.error)
                }
            }
        }.shared()

    private val apiException: Flow<Exception?> =
        paymentResponse.flatMapLatest {
            flow {
                if (it.isError && it.exception != null) {
                    emit(it.exception)
                }
            }
        }.shared()

    private val finalRedirect = MutableStateFlow<PaytrailPaymentRedirect?>(null)
    private val webviewException = MutableStateFlow<Exception?>(null)

    private val paymentException = combine(apiException, webviewException) { a, b -> a ?: b }

    val paymentState: Flow<PaytrailPaymentState> = combine(
        flow = finalRedirect.onStart { emit(null) },
        flow2 = apiErrorResponse.onStart { emit(null) },
        flow3 = paymentException.onStart { emit(null) },
        flow4 = tokenPaymentResponse.onStart { emit(null) },
        transform = {
                redirect: PaytrailPaymentRedirect?,
                errorResponse: PaytrailApiErrorResponse?,
                exception: Exception?,
                response: TokenPaymentResponse?,
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

                response != null -> PaytrailPaymentState(
                    state = PaytrailPaymentState.State.PAYMENT_OK,
                    tokenPaymentResponse = response,
                )

                else -> PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS)
            }
        },
    ).shared()

    private suspend fun callTokenPaymentApi(
        tokenPaymentRequest: TokenPaymentRequest,
    ): Response<TokenPaymentResponse> = when (paymentType) {
        TokenPaymentType.CIT -> when (chargeType) {
            TokenPaymentChargeType.CHARGE -> {
                api.tokenCitCharge(tokenPaymentRequest)
            }

            TokenPaymentChargeType.AUTH_HOLD -> {
                api.tokenCitAuthorizationHold(tokenPaymentRequest)
            }
        }

        TokenPaymentType.MIT -> when (chargeType) {
            TokenPaymentChargeType.CHARGE -> {
                api.tokenMitCharge(tokenPaymentRequest)
            }

            TokenPaymentChargeType.AUTH_HOLD -> {
                api.tokenMitAuthorizationHold(tokenPaymentRequest)
            }
        }
    }

    fun finalRedirectReceived(uri: Uri) {
        finalRedirect.value = PaytrailPaymentRedirect(uri)
    }

    fun webviewErrorReceived(exception: Exception) {
        webviewException.value = exception
    }

    private fun <T> Flow<T>.shared(): Flow<T> = shareIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        replay = 1,
    )
}
