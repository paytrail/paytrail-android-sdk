package fi.paytrail.demo.payments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fi.paytrail.demo.di.ViewModelParamTransactionId
import fi.paytrail.paymentsdk.RequestStatus
import fi.paytrail.sdk.apiclient.models.TokenPaymentRequest
import fi.paytrail.sdk.apiclient.models.TokenPaymentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsViewModel @Inject constructor(
    @ViewModelParamTransactionId val paymentId: UUID,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {

    private val refreshPaymentDetailsTrigger = MutableStateFlow(0)

    val paymentData = paymentRepository.getPaymentData(paymentId)
    val paymentDetails = refreshPaymentDetailsTrigger.flatMapLatest {
        paymentRepository.getPaymentDetails(paymentId)
    }.shareIn(scope = viewModelScope, started = SharingStarted.Lazily, replay = 1)

    private fun refreshPaymentDetails() {
        refreshPaymentDetailsTrigger.value++
    }

    val authHoldActionState = MutableStateFlow<RequestStatus<TokenPaymentResponse>?>(null)

    fun commit(tokenPaymentRequest: TokenPaymentRequest? = null) {
        viewModelScope.launch {
            if (tokenPaymentRequest != null) {
                paymentRepository.commit(paymentId, tokenPaymentRequest).collect {
                    Log.i("PaymentDetailsViewModel", "commit result: $it")
                    if (!it.isLoading) refreshPaymentDetails()
                    authHoldActionState.value = it
                }
            } else {
                paymentRepository.commit(paymentId).collect {
                    Log.i("PaymentDetailsViewModel", "commit result: $it")
                    if (!it.isLoading) refreshPaymentDetails()
                    authHoldActionState.value = it
                }
            }
        }
    }

    fun revert() {
        viewModelScope.launch {
            paymentRepository.revert(paymentId).collect {
                Log.i("PaymentDetailsViewModel", "revert result: $it")
                if (it.isSuccess) refreshPaymentDetails()
                authHoldActionState.value = it
            }
        }
    }
}
