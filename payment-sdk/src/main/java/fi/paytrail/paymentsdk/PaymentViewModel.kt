package fi.paytrail.paymentsdk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.ApiClient
import fi.paytrail.sdk.apiclient.models.PaymentMethodGroupData
import fi.paytrail.sdk.apiclient.models.PaymentMethodProvider
import fi.paytrail.sdk.apiclient.models.PaymentRequest

data class PaymentMethodGroup(
    val paymentMethodGroup: PaymentMethodGroupData,
    val paymentMethods: List<PaymentMethod>,
) {
    val id = paymentMethodGroup.id
    val name = paymentMethodGroup.name
    val svg = paymentMethodGroup.svg
}

data class PaymentMethod(
    val provider: PaymentMethodProvider,
) {
    val id: String = provider.id
    val name: String = provider.name
    val svg: String = provider.svg
    val formParameters: String by lazy {
        provider.parameters.joinToString(separator = "&") { "${it.name}=${it.value}" }
    }
}

class PaymentViewModel(
    val paymentRequest: PaymentRequest,
    val merchantAccount: MerchantAccount,
) : ViewModel() {

    private val api by lazy {
        with(ApiClient(merchantAccount = merchantAccount)) {
            setLogger { Log.i("OkHttp", it) }
            createService(PaymentsApi::class.java)
        }
    }

    // TODO: Add mechanism to trigger retrying the createPayment request?
    val paymentProviderListing = liveData {
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
            // TODO: emit errors
            emit(emptyList())
        }
    }

    val selectedPaymentProvider = MutableLiveData<PaymentMethod?>(null)

    fun startPayment(provider: PaymentMethod) {
        selectedPaymentProvider.postValue(provider)
    }

    fun cancelPayment() {
        selectedPaymentProvider.postValue(null)
    }

    // TODO: This probably should be a MediatorLiveData, observing other request statuses,
    //  and showing result accordingly. Rough idea:
    //    * if payment is complete, PAYMENT_COMPLETE
    //    * if payment failed, PAYMENT_ERROR
    //    * if payment provider has been selected, status is PAYMENT_IN_PROGRESS
    //    * if payment providers are loaded, status is SHOW_PAYMENT_PROVIDERS
    //    * until payment providers are loaded, status is LOADING_PAYMENT_PROVIDERS
    val paymentStatus: LiveData<PaytrailPaymentStatus> =
        MediatorLiveData(PaytrailPaymentStatus.LOADING_PAYMENT_PROVIDERS).apply {
            var hasPPsLoaded = false
            var isPPSelected = false

            fun refreshState() {
                value = when {
                    isPPSelected -> PaytrailPaymentStatus.PAYMENT_IN_PROGRESS
                    hasPPsLoaded -> PaytrailPaymentStatus.SHOW_PAYMENT_PROVIDERS
                    else -> PaytrailPaymentStatus.LOADING_PAYMENT_PROVIDERS
                }
            }

            addSource(paymentProviderListing) {
                // TODO: This needs improving; we need to be looking at the create_payment
                //       request & response, and set state accordingly (loading/ok/error)
                hasPPsLoaded = true
                refreshState()
            }

            addSource(selectedPaymentProvider) {
                isPPSelected = it != null
                refreshState()
            }
        }
}

enum class PaytrailPaymentStatus {
    LOADING_PAYMENT_PROVIDERS,
    SHOW_PAYMENT_PROVIDERS,
    PAYMENT_IN_PROGRESS,
    PAYMENT_ERROR,
    PAYMENT_CANCELED,
    PAYMENT_DONE,
}
