package fi.paytrail.paymentsdk

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.paytrail.paymentsdk.model.PaytrailPaymentState
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient
import fi.paytrail.sdk.apiclient.models.PaymentRequest

/**
 * A compose view that streamlines the payment process by integrating both the payment and card tokenization in a single step.
 *
 * This component is designed to enhance the user experience by allowing them to perform a payment while simultaneously saving their card details for future transactions. It simplifies the two-step process of paying and then saving card information into a single, seamless step.
 *
 * @param modifier The modifier to be applied to the Composable for adjusting layout behavior.
 * @param paymentRequest Contains details of the payment like amount, reference, etc.
 * @param onPaymentStateChanged Callback invoked when there's any change in the payment state.
 * @param merchantAccount Contains merchant's account details.
 * @param apiClient Client for connecting to the Paytrail API. Defaults to a new instance if not provided.
 */
@Composable
fun PayAndAddCard(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
    apiClient: PaytrailApiClient = PaytrailApiClient(merchantAccount = merchantAccount),
) {
    val viewModel: PayAndAddCardViewModel = viewModel(
        factory = PayAndAddCardViewModelFactory(paymentRequest, apiClient),
    )

    PayAndAddCard(
        modifier = modifier,
        viewModel = viewModel,
        onPaymentStateChanged = onPaymentStateChanged,
        merchantAccount = merchantAccount,
    )
}

@Composable
internal fun PayAndAddCard(
    modifier: Modifier,
    viewModel: PayAndAddCardViewModel,
    onPaymentStateChanged: PaymentStateChangeListener,
    merchantAccount: MerchantAccount,
) {
    val paymentStatus =
        viewModel.paymentState.collectAsState(
            initial = PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged.onPaymentStateChanged(paymentStatus)
    }

    val url: String? = viewModel.paymentRedirectUrl.collectAsState(initial = null).value

    Surface(modifier) {
        if (url != null) {
            PaytrailWebView(
                modifier = Modifier.fillMaxSize(),
                url = url,
                redirectUrls = viewModel.paymentRequest.redirectUrls,
                onFinalRedirect = viewModel::finalRedirectReceived,
                onError = viewModel::webviewErrorReceived,
                signatureVerificationSecret = merchantAccount.secret,
            )
        } else {
            LoadingIndicator()
        }
    }
}
