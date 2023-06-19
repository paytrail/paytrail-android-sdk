package fi.paytrail.paymentsdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.models.PaymentRequest

class PaymentViewModelFactory(
    val paymentOrder: PaymentRequest,
    val merchantAccount: MerchantAccount,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(paymentOrder, merchantAccount) as T
    }
}
