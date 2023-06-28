package fi.paytrail.paymentsdk

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.LOADING_PAYMENT_METHODS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_IN_PROGRESS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.SHOW_PAYMENT_METHODS
import fi.paytrail.sdk.apiclient.models.PaymentRequest
import java.util.concurrent.atomic.AtomicInteger

private val paymentCompositionCounter = AtomicInteger(0)

@Composable
fun PaytrailPayment(
    modifier: Modifier = Modifier,
    payment: PaymentRequest,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
) {
    val paymentCompositionId =
        remember(payment) { "payment-${paymentCompositionCounter.incrementAndGet()}" }

    val localView = LocalView.current
    val viewModel = remember(paymentCompositionId) {
        val viewModelStoreOwner = localView.findViewTreeViewModelStoreOwner() ?: run {
            throw RuntimeException("No ViewModelStoreOwner associated with local view $localView")
        }

        ViewModelProvider(
            viewModelStoreOwner,
            PaymentViewModelFactory(payment),
        )[paymentCompositionId, PaymentViewModel::class.java]
    }

    PaytrailPayment(
        modifier = modifier,
        viewModel = viewModel,
        onPaymentStateChanged = onPaymentStateChanged,
    )
}

@Composable
internal fun PaytrailPayment(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
) {
    val paymentStatus =
        viewModel.paymentState.observeAsState(
            initial = PaytrailPaymentState(
                LOADING_PAYMENT_METHODS,
            ),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged(paymentStatus)
    }

    // TODO: Set up & apply custom theming to relevant components

    Surface(modifier) {
        when (paymentStatus.state) {
            LOADING_PAYMENT_METHODS -> LoadingPaymentMethods(modifier = Modifier.fillMaxSize())

            SHOW_PAYMENT_METHODS -> PaymentProviders(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )

            PAYMENT_IN_PROGRESS -> PayWithPaymentMethod(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )

            else -> {
                // No content for complete/error/cancel states. Application should remove PaytrailPayment
                // from composition, or remove PaytrailPaymentFragment from view tree.
            }
        }
    }

    BackHandler { viewModel.onBackNavigation() }
}
