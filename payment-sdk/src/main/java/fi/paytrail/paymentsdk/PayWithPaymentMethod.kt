package fi.paytrail.paymentsdk

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import fi.paytrail.paymentsdk.model.PaymentMethod
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.sdk.apiclient.MerchantAccount

@Composable
fun PayWithPaymentMethod(
    modifier: Modifier = Modifier,
    merchantAccount: MerchantAccount,
    viewModel: PaymentViewModel,
) {
    val paymentMethod: PaymentMethod? = viewModel.selectedPaymentProvider.observeAsState(null).value

    if (paymentMethod != null) {
        PaytrailWebView(
            modifier = modifier,
            url = paymentMethod.provider.url,
            method = PaytrailWebViewCallMethod.POST,
            postParameters = paymentMethod.formParameters,
            redirectUrls = viewModel.paymentRequest.redirectUrls,
            onFinalRedirect = { viewModel.onPaymentRedirect(PaytrailPaymentRedirect(it)) },
            signatureVerificationSecret = merchantAccount.secret,
            onError = viewModel::onPaymentError,
        )
    }
}
