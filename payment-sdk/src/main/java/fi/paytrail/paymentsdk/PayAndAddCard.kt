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

@Composable
fun PayAndAddCard(
    modifier: Modifier = Modifier,
    paymentRequest: PaymentRequest,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit, // TODO: Replace with functional interface! for java compatibility
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
fun PayAndAddCard(
    modifier: Modifier,
    viewModel: PayAndAddCardViewModel,
    onPaymentStateChanged: (PaytrailPaymentState) -> Unit,
    merchantAccount: MerchantAccount,
) {
    val paymentStatus =
        viewModel.paymentState.collectAsState(
            initial = PaytrailPaymentState(PaytrailPaymentState.State.PAYMENT_IN_PROGRESS),
        ).value

    LaunchedEffect(paymentStatus) {
        onPaymentStateChanged(paymentStatus)
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
