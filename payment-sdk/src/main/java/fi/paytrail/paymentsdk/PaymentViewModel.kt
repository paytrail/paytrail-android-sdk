package fi.paytrail.paymentsdk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import fi.paytrail.paymentsdk.model.PaymentMethod
import fi.paytrail.paymentsdk.model.PaymentMethodGroup
import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect.Status.Fail
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect.Status.Ok
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.LOADING_PAYMENT_PROVIDERS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_IN_PROGRESS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.SHOW_PAYMENT_PROVIDERS
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.ErrorResponse.Companion.deserialize
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import fi.paytrail.sdk.apiclient.models.PaymentRequestResponse
import retrofit2.Response

class PaymentViewModel(
    val paymentRequest: PaymentRequest,
    apiClient: PaytrailApiClient,
) : ViewModel() {

    private val api by lazy { apiClient.createService(PaymentsApi::class.java) }

    val createPaymentResponse: LiveData<Response<PaymentRequestResponse>> = liveData {
        try {
            emit(api.createPayment(paymentRequest = paymentRequest))
        } catch (e: Exception) {
            PaytrailLogger.i("Error in loading payment providers ${e.message}")
            paymentError.postValue(e)
        }
    }

    val paymentTerms: LiveData<String> = createPaymentResponse.map {
        if (it.isSuccessful) {
            it.body()?.terms ?: ""
        } else {
            ""
        }
    }

    val paymentMethodGroups: LiveData<List<PaymentMethodGroup>> =
        createPaymentResponse.map { result ->
            try {
                if (result.isSuccessful) {
                    val body = result.body()

                    val providers = body?.providers ?: emptyList()
                    val groups = body?.groups ?: emptyList()

                    groups.map { groupData ->
                        PaymentMethodGroup(
                            paymentMethodGroup = groupData,
                            paymentMethods = providers
                                .filter { provider -> provider.group.value == groupData.id.value }
                                .map { PaymentMethod(it) },
                        )
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                PaytrailLogger.i("Error in loading payment providers ${e.message}")
                paymentError.postValue(e)
                emptyList()
            }
        }

    val selectedPaymentProvider = MutableLiveData<PaymentMethod?>(null)
    private val paymentWebViewRedirect = MutableLiveData<PaytrailPaymentRedirect>()
    private val paymentError = MutableLiveData<Exception>()

    private val apiErrorResponse = createPaymentResponse.switchMap { result ->
        liveData {
            if (!result.isSuccessful) {
                val errorBody = result.errorBody()?.string()
                val errorResponse = errorBody?.let {
                    try {
                        deserialize(it)
                    } catch (e: IllegalArgumentException) {
                        PaytrailLogger.w("Failed deserializing error body: $errorBody")
                        null
                    }
                }
                emit(
                    PaytrailApiErrorResponse(
                        code = result.code(),
                        errorBody = errorBody,
                        errorResponse = errorResponse,
                    ),
                )
            }
        }
    }
    private val paymentCanceled = MutableLiveData(false)

    fun startPayment(provider: PaymentMethod) {
        selectedPaymentProvider.postValue(provider)
    }

    fun onBackNavigation() {
        if (selectedPaymentProvider.value != null) {
            selectedPaymentProvider.postValue(null)
        } else {
            paymentCanceled.postValue(true)
        }
    }

    fun onPaymentRedirect(redirect: PaytrailPaymentRedirect) {
        this.paymentWebViewRedirect.postValue(redirect)
    }

    fun onPaymentError(exception: Exception) {
        this.paymentError.postValue(exception)
    }
    val paymentState: LiveData<PaytrailPaymentState> =
        MediatorLiveData(PaytrailPaymentState(LOADING_PAYMENT_PROVIDERS)).apply {
            var paymentProvidersLoaded = false
            var selectedMethod: PaymentMethod? = null
            var redirect: PaytrailPaymentRedirect? = null
            var error: Exception? = null
            var errorResponse: PaytrailApiErrorResponse? = null
            var canceled = false

            fun refreshState() {
                value = when {
                    canceled -> PaytrailPaymentState(PAYMENT_CANCELED)

                    errorResponse != null || error != null || redirect?.status == Fail -> PaytrailPaymentState(
                        state = PAYMENT_FAIL,
                        finalRedirectRequest = redirect,
                        apiErrorResponse = errorResponse,
                        exception = error,
                    )

                    redirect?.status == Ok -> PaytrailPaymentState(
                        state = PAYMENT_OK,
                        finalRedirectRequest = redirect,
                    )

                    selectedMethod != null -> PaytrailPaymentState(PAYMENT_IN_PROGRESS)
                    paymentProvidersLoaded -> PaytrailPaymentState(SHOW_PAYMENT_PROVIDERS)
                    else -> PaytrailPaymentState(LOADING_PAYMENT_PROVIDERS)
                }
            }

            addSource(paymentMethodGroups) {
                paymentProvidersLoaded = true
                refreshState()
            }

            addSource(selectedPaymentProvider) {
                selectedMethod = it
                refreshState()
            }

            addSource(paymentWebViewRedirect) {
                redirect = it
                refreshState()
            }

            addSource(paymentError) {
                error = it
                refreshState()
            }

            addSource(apiErrorResponse) {
                errorResponse = it
                refreshState()
            }

            addSource(paymentCanceled) {
                canceled = it
                refreshState()
            }
        }
}
