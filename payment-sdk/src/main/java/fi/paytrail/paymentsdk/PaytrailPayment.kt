package fi.paytrail.paymentsdk

import android.net.Uri
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import java.util.concurrent.atomic.AtomicInteger

private val paymentCompositionCounter = AtomicInteger(0)

data class PaytrailPaymentState(
    val paymentStatus: PaytrailPaymentStatus,
    val transactionId: String,
    val paymentResult: PaytrailPaymentResult, // XXX: Change this to API model?
)

data class PaytrailPaymentResult(val redirectUri: Uri) {
    val account: Int by lazy { redirectUri.getQueryParameter("checkout-account")!!.toInt() }
    val algorithm: String by lazy { redirectUri.getQueryParameter("checkout-algorithm")!! }
    val amount: Int by lazy { redirectUri.getQueryParameter("checkout-amount")!!.toInt() }
    val settlementReference: String? by lazy { redirectUri.getQueryParameter("checkout-settlement-reference") }
    val stamp: String by lazy { redirectUri.getQueryParameter("checkout-stamp")!! }
    val reference: String by lazy { redirectUri.getQueryParameter("checkout-reference")!! }
    val transactionId: String by lazy { redirectUri.getQueryParameter("checkout-transaction-id")!! }
    val status: Status by lazy { Status.fromQueryParamString(redirectUri.getQueryParameter("checkout-status")!!) }
    val provider: String by lazy { redirectUri.getQueryParameter("checkout-provider")!! }
    val signature: String by lazy { redirectUri.getQueryParameter("signature")!! }

    enum class Status(val s: String) {
        New("new"),
        Ok("ok"),
        Fail("fail"),
        Pending("pending"),
        Delayed("delayed"),
        ;

        companion object {
            fun fromQueryParamString(s: String): Status = values().first { it.s == s }
        }
    }
}

@Composable
fun PaytrailPayment(
    modifier: Modifier,
    payment: PaymentRequest,
    merchant: MerchantAccount = MerchantAccount.default,
    onPaymentResult: (PaytrailPaymentResult) -> Unit,
) {
    val paymentCompositionId = remember(
        keys = arrayOf(
            payment,
            merchant,
        ),
    ) { "payment-${paymentCompositionCounter.incrementAndGet()}" }

    val localView = LocalView.current
    val viewModel = remember(paymentCompositionId) {
        val viewModelStoreOwner = localView.findViewTreeViewModelStoreOwner() ?: run {
            throw RuntimeException("No ViewModelStoreOwner associated with local view $localView")
        }

        ViewModelProvider(
            viewModelStoreOwner,
            PaymentViewModelFactory(payment, merchant),
        )[paymentCompositionId, PaymentViewModel::class.java]
    }

    PaytrailPayment(
        modifier = modifier,
        viewModel = viewModel,
        onPaymentResult = onPaymentResult,
    )
}

@Composable
internal fun PaytrailPayment(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel,
    onPaymentResult: (PaytrailPaymentResult) -> Unit,
) {
    val paymentStatus =
        viewModel.paymentStatus.observeAsState(initial = PaytrailPaymentStatus.LOADING_PAYMENT_PROVIDERS).value

    // TODO: Set up & apply custom theming to relevant components

    Surface(modifier) {
        when (paymentStatus) {
            PaytrailPaymentStatus.LOADING_PAYMENT_PROVIDERS -> LoadingPaymentMethods()
            PaytrailPaymentStatus.SHOW_PAYMENT_PROVIDERS -> PaymentProviders(viewModel = viewModel)
            PaytrailPaymentStatus.PAYMENT_IN_PROGRESS -> PaymentWebView(
                viewModel = viewModel,
                onPaymentResult = onPaymentResult,
            )

            PaytrailPaymentStatus.PAYMENT_ERROR -> TODO()
            PaytrailPaymentStatus.PAYMENT_CANCELED -> TODO()
            PaytrailPaymentStatus.PAYMENT_DONE -> TODO()
        }
    }
}
