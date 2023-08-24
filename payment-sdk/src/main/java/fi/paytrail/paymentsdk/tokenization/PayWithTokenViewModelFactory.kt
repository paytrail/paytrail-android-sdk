package fi.paytrail.paymentsdk.tokenization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

class PayWithTokenViewModelFactory(
    private val tokenizationId: String? = null,
    private val token: String? = null,
    private val paymentRequest: PaymentRequest,
    private val paymentType: TokenPaymentType,
    private val chargeType: TokenPaymentChargeType,
    private val apiClient: PaytrailApiClient,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PayWithTokenViewModel(
            tokenizationId = tokenizationId,
            token = token,
            paymentRequest = paymentRequest,
            paymentType = paymentType,
            chargeType = chargeType,
            apiClient = apiClient,
        ) as T
    }
}
