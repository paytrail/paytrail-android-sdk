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
import fi.paytrail.sdk.apiclient.infrastructure.ApiClient
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
) : ViewModel() {

    private val api by lazy {
        ApiClient().createService(TokenPaymentsApi::class.java)
    }

    private val paymentToken: Flow<RequestStatus<String>> = flow {
        if (token != null) {

            emit(RequestStatus.success(token))
        } else if (tokenizationId != null) {
            emit(RequestStatus.loading())
            try {
                val response = api.requestTokenForTokenizationId(tokenizationId)
                if (response.isSuccessful) {
                    val tokenizationRequestResponse = requireNotNull(response.body())
                    emit(RequestStatus.success(tokenizationRequestResponse.token))
                } else {
                    val errorResponse = createErrorResponse(response)
                    emit(RequestStatus.error(errorResponse))
                }
            } catch (e: Exception) {
                emit(RequestStatus.error(exception = e))
            }
        } else {
            throw IllegalArgumentException("Either token or tokenizationId must be provided")
        }
    }.shared()

    private val paymentResponse: Flow<RequestStatus<out TokenPaymentResponse>> = paymentToken.map {
        when {
            it.isSuccess -> {
                val paymentToken = requireNotNull(it.value)
                val response =
                    callTokenPaymentApi(paymentRequest.combineWithToken(paymentToken))
                try {
                    if (response.isSuccessful) {
                        RequestStatus.success(requireNotNull(response.body()))
                    } else if (response.code() == 403) {
                        // 403 indicates a redirect to 3DS verification. It is not an error
                        // for token payment APIs, app just needs to be showing the 3DS flow
                        // in a webview.
                        val string = response.errorBody()?.string()
                        val value = TokenPaymentResponse.deserialize(string ?: "")
                        RequestStatus.success(value)
                    } else {
                        val errorResponse = createErrorResponse(response)
                        RequestStatus.error(errorResponse)
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

    val transactionId = paymentResponse.map {
        if (it.isSuccess) it.value!!.transactionId else null
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

    private val finalRedirect = MutableStateFlow<PaytrailPaymentRedirect?>(null)
    private val apiException = MutableStateFlow<Exception?>(null)

    val paymentState: Flow<PaytrailPaymentState> = combine(
        flow = finalRedirect.onStart { emit(null) },
        flow2 = apiErrorResponse.onStart { emit(null) },
        flow3 = apiException.onStart { emit(null) },
        flow4 = tokenPaymentResponse.onStart { emit(null) },
        transform = {
                redirect: PaytrailPaymentRedirect?,
                errorResponse: PaytrailApiErrorResponse?,
                error: Exception?,
                response: TokenPaymentResponse?,
            ->
            when {

                errorResponse != null || error != null || redirect?.status == PaytrailPaymentRedirect.Status.Fail -> PaytrailPaymentState(
                    state = PaytrailPaymentState.State.PAYMENT_FAIL,
                    finalRedirectRequest = redirect,
                    apiErrorResponse = errorResponse,
                    exception = error,
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
    )

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

    fun errorReceived(exception: Exception) {
        apiException.value = exception
    }

    private fun <T> Flow<T>.shared(): Flow<T> = shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1,
    )
}

private fun PaymentRequest.combineWithToken(token: String): TokenPaymentRequest =
    TokenPaymentRequest(
        stamp = stamp,
        reference = reference,
        amount = amount,
        currency = currency,
        language = language,
        items = items,
        customer = customer,
        redirectUrls = redirectUrls,
        token = token,
        orderId = orderId,
        deliveryAddress = deliveryAddress,
        invoicingAddress = invoicingAddress,
        callbackUrls = callbackUrls,
        callbackDelay = callbackDelay,
    )
