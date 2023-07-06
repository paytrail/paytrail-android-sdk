package fi.paytrail.demo.payments

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentListingViewModel @Inject constructor(
    repo: PaymentRepository,
) : ViewModel() {
    val payments = repo.payments()
}
