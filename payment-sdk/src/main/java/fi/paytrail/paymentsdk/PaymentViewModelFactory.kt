package fi.paytrail.paymentsdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.paytrail.sdk.apiclient.models.PaymentRequest

class PaymentViewModelFactory(
    private val paymentRequest: PaymentRequest,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentViewModel(paymentRequest) as T
    }
}
