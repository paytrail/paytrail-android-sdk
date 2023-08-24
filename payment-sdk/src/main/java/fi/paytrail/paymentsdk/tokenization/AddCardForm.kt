package fi.paytrail.paymentsdk.tokenization

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fi.paytrail.paymentsdk.PaytrailWebView
import fi.paytrail.paymentsdk.PaytrailWebViewCallMethod
import fi.paytrail.paymentsdk.tokenization.model.AddCardRequest
import fi.paytrail.paymentsdk.tokenization.model.AddCardResult
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailHmacCalculator
import fi.paytrail.sdk.apiclient.models.AddCardFormRequest
import fi.paytrail.sdk.apiclient.models.Callbacks
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class AddCardRedirect(val url: Uri) {
    val account = url.getQueryParameter("checkout-account")
    val algorithm = url.getQueryParameter("checkout-algorithm")
    val method = url.getQueryParameter("checkout-method")
    val status = url.getQueryParameter("checkout-status")
    val tokenizationId = url.getQueryParameter("checkout-tokenization-id")
    val signature = url.getQueryParameter("signature")
}

@Composable
fun AddCardForm(
    modifier: Modifier = Modifier,
    request: AddCardRequest,
    merchantAccount: MerchantAccount,
    onAddCardResult: (AddCardResult) -> Unit,
) {
    val addCardFormRequest =
        AddCardFormRequest(
            checkoutAccount = merchantAccount.id,
            checkoutMethod = "POST",
            checkoutAlgorithm = PaytrailHmacCalculator.ALGORITHM_SHA512,
            checkoutRedirectSuccessUrl = request.redirectUrls.success,
            checkoutRedirectCancelUrl = request.redirectUrls.cancel,
            checkoutCallbackSuccessUrl = request.callbackUrls?.success,
            checkoutCallbackCancelUrl = request.callbackUrls?.cancel,
            language = request.language,
            checkoutNonce = UUID.randomUUID().toString(),
            checkoutTimestamp = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(OffsetDateTime.now()),
        ).withSignature(account = merchantAccount)

    AddCardForm(
        modifier = modifier,
        request = addCardFormRequest,
        onAddCardStatusChanged = onAddCardResult,
        merchantAccount = merchantAccount,
    )
}

@Composable
private fun AddCardForm(
    modifier: Modifier = Modifier,
    request: AddCardFormRequest,
    onAddCardStatusChanged: (AddCardResult) -> Unit,
    merchantAccount: MerchantAccount,
) {
    PaytrailWebView(
        modifier = modifier,
        method = PaytrailWebViewCallMethod.POST,
        postParameters = request.asPostParams(),
        url = "https://services.paytrail.com/tokenization/addcard-form",
        redirectUrls = Callbacks(
            success = request.checkoutRedirectSuccessUrl,
            cancel = request.checkoutRedirectCancelUrl,
        ),
        onFinalRedirect = {
            val redirect = AddCardRedirect(it)
            val state = if (redirect.status == "ok") {
                AddCardResult.Result.SUCCESS
            } else {
                AddCardResult.Result.FAILURE
            }
            onAddCardStatusChanged(
                AddCardResult(
                    result = state,
                    redirect = redirect,
                ),
            )
        },
        onError = {
            onAddCardStatusChanged(
                AddCardResult(
                    result = AddCardResult.Result.ERROR,
                    exception = it,
                ),
            )
        },
        signatureVerificationSecret = merchantAccount.secret,
        allowBackNavigation = false,
    )
}
