package fi.paytrail.paymentsdk

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.LOADING_PAYMENT_PROVIDERS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.PAYMENT_IN_PROGRESS
import fi.paytrail.paymentsdk.model.PaytrailPaymentState.State.SHOW_PAYMENT_PROVIDERS
import fi.paytrail.paymentsdk.typography.Poppins
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

@Composable
fun PaytrailPayment(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit, // TODO: Replace with functional interface for java compatibility
    merchantAccount: MerchantAccount,
    apiClient: PaytrailApiClient = PaytrailApiClient(merchantAccount = merchantAccount),
) {
    val viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(paymentRequest, apiClient),
    )

    // TODO: get the font family from current theme?
    CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = Poppins)) {

        PaytrailPayment(
            modifier = modifier,
            viewModel = viewModel,
            onPaymentStateChanged = onPaymentStateChanged,
            merchantAccount = merchantAccount,
        )

    }
}

@Composable
internal fun PaytrailPayment(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
    merchantAccount: MerchantAccount,
) {
    val paymentStatus =
        viewModel.paymentState.observeAsState(
            initial = PaytrailPaymentState(
                LOADING_PAYMENT_PROVIDERS,
            ),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged(paymentStatus)
    }

    // TODO: Set up & apply custom theming to relevant components

    Surface(modifier) {
        when (paymentStatus.state) {
            LOADING_PAYMENT_PROVIDERS -> LoadingIndicator(
                modifier = Modifier.fillMaxSize()
                    .semantics { testTag = "PaymentProvidersLoadingIndicator" },
            )

            SHOW_PAYMENT_PROVIDERS -> PaymentProviders(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )

            PAYMENT_IN_PROGRESS -> PayWithPaymentMethod(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                merchantAccount = merchantAccount,
            )

            else -> {
                // No content for complete/error/cancel states. Application should remove PaytrailPayment
                // from composition, or remove PaytrailPaymentFragment from view tree.
            }
        }
    }

    BackHandler { viewModel.onBackNavigation() }
}
