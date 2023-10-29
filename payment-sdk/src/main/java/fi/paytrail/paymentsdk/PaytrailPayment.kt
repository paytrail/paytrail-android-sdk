package fi.paytrail.paymentsdk

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
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
import fi.paytrail.paymentsdk.theme.Poppins
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest
/**
 * A Composable function representing the Paytrail payment view.
 * This view displays a list of available payment providers. Upon selecting a provider,
 * the user is redirected to a WebView where the actual payment process takes place
 * using the chosen payment provider.
 * @param modifier Optional [Modifier] for adjusting the layout of this composable.
 * @param paymentRequest Contains details of the payment like amount, reference, etc.
 * @param onPaymentStateChanged Callback triggered when there's a change in the payment process state.
 * @param merchantAccount Merchant's Paytrail account details.
 * @param apiClient the http client for making API calls to Paytrail servers. By default, we use our internal one with the provided merchant account.
 */
@Composable
fun PaytrailPayment(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
    apiClient: PaytrailApiClient = PaytrailApiClient(merchantAccount = merchantAccount),
) {
    val viewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(paymentRequest, apiClient),
    )

    // TODO: Get colors from current theme
    // TODO: Make font family themable?
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
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
) {
    val paymentStatus =
        viewModel.paymentState.observeAsState(
            initial = PaytrailPaymentState(
                LOADING_PAYMENT_PROVIDERS,
            ),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged.onPaymentStateChanged(paymentStatus)
    }

    // TODO: Set up & apply custom theming to relevant components

    Box(modifier = modifier) {
        when (paymentStatus.state) {
            LOADING_PAYMENT_PROVIDERS -> {
                LoadingIndicator(
                    modifier = Modifier.fillMaxSize()
                        .semantics { testTag = "PaymentProvidersLoadingIndicator" },
                )
            }

            SHOW_PAYMENT_PROVIDERS -> PaymentProviders(
                modifier = Modifier.fillMaxSize().height(IntrinsicSize.Min),
                viewModel = viewModel,
            )

            PAYMENT_IN_PROGRESS -> PayWithPaymentMethod(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                merchantAccount = merchantAccount,
            )

            else -> {
                // No content for complete/error/cancel states. Application should remove PaytrailPayment
                // from composition, or remove containing fragment/activity from backstack
            }
        }
    }

    BackHandler { viewModel.onBackNavigation() }
}
