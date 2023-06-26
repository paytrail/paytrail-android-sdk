package fi.paytrail.paymentsdk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import fi.paytrail.paymentsdk.model.PaymentMethod
import fi.paytrail.paymentsdk.model.PaymentMethodGroup
import fi.paytrail.paymentsdk.model.PaytrailApiErrorResponse
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect.Status.Fail
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect.Status.Ok
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.LOADING_PAYMENT_METHODS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_CANCELED
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_FAIL
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_IN_PROGRESS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_OK
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.SHOW_PAYMENT_METHODS
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.ApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

class PaymentViewModel(
    val paymentRequest: PaymentRequest,
    val merchantAccount: MerchantAccount,
) : ViewModel() {

    private val api by lazy {
        ApiClient(
            merchantAccount = merchantAccount,
            okHttpClientBuilder = PaytrailBaseOkHttpClient.baseClient?.newBuilder(),
        ).createService(PaymentsApi::class.java)
    }

    // TODO: Add mechanism to trigger retrying the createPayment request?
    val paymentProviderListing = liveData {
        try {
            val result = api.createPayment(paymentRequest = paymentRequest)
            if (result.isSuccessful) {
                val body = result.body()

                val providers = body?.providers ?: emptyList()
                val groups = body?.groups ?: emptyList()

                emit(
                    groups.map { groupData ->
                        PaymentMethodGroup(
                            paymentMethodGroup = groupData,
                            paymentMethods = providers
                                .filter { provider -> provider.group.value == groupData.id.value }
                                .map { PaymentMethod(it) },
                        )
                    },
                )
            } else {
                Log.i("PaymentViewModel", "API error: $result")
                apiErrorResponse.postValue(PaytrailApiErrorResponse(result))
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.i("PaymentViewModel", "Error in loading payment providers", e)
            paymentError.postValue(e)
        }
    }

    val selectedPaymentProvider = MutableLiveData<PaymentMethod?>(null)
    private val paymentWebViewRedirect = MutableLiveData<PaytrailPaymentRedirect>()
    private val paymentError = MutableLiveData<Exception>()
    private val apiErrorResponse = MutableLiveData<PaytrailApiErrorResponse>()
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

    // TODO: This probably should be a MediatorLiveData, observing other request statuses,
    //  and showing result accordingly. Rough idea:
    //    * if payment is complete, PAYMENT_COMPLETE
    //    * if payment failed, PAYMENT_ERROR
    //    * if payment provider has been selected, status is PAYMENT_IN_PROGRESS
    //    * if payment providers are loaded, status is SHOW_PAYMENT_PROVIDERS
    //    * until payment providers are loaded, status is LOADING_PAYMENT_PROVIDERS
    val paymentState: LiveData<PaytrailPaymentState> =
        MediatorLiveData(PaytrailPaymentState(LOADING_PAYMENT_METHODS)).apply {
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
                        redirectRequest = redirect,
                        exception = error,
                    )

                    redirect?.status == Ok -> PaytrailPaymentState(
                        state = PAYMENT_OK,
                        redirectRequest = redirect,
                    )

                    selectedMethod != null -> PaytrailPaymentState(PAYMENT_IN_PROGRESS)
                    paymentProvidersLoaded -> PaytrailPaymentState(SHOW_PAYMENT_METHODS)
                    else -> PaytrailPaymentState(LOADING_PAYMENT_METHODS)
                }
            }

            addSource(paymentProviderListing) {
                // TODO: This needs improving; we need to be looking at the create_payment
                //       request & response, and set state accordingly (loading/ok/error)
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
