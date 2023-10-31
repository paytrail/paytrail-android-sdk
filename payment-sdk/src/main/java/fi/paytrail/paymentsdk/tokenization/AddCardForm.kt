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
/**
 * Represents the redirected URL data after the card has been added.
 *
 * @param url The redirected URL after card tokenization.
 */
data class AddCardRedirect(val url: Uri) {
    val account = url.getQueryParameter("checkout-account")
    val algorithm = url.getQueryParameter("checkout-algorithm")
    val method = url.getQueryParameter("checkout-method")
    val status = url.getQueryParameter("checkout-status")
    val tokenizationId = url.getQueryParameter("checkout-tokenization-id")
    val signature = url.getQueryParameter("signature")
}
interface AddCardStatusChangedListener {
    fun onAddCardResult(addCardResult: AddCardResult)
}

/**
 * A Compose view that provides a webview-based form for users to add their payment card information.
 *
 * This form focuses on card tokenization without initiating a payment. It's a user-friendly method for developers
 * to tokenize a user's card information.
 *
 * @param modifier Compose UI modifier.
 * @param request The request details required to set up the card form.
 * @param merchantAccount The merchant's Paytrail account details.
 * @param onAddCardResult Callback for the result after a card has been added.
 */
@Composable
fun AddCardForm(
    modifier: Modifier = Modifier,
    request: AddCardRequest,
    merchantAccount: MerchantAccount,
    onAddCardResult: AddCardStatusChangedListener,
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
    onAddCardStatusChanged: AddCardStatusChangedListener,
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
            onAddCardStatusChanged.onAddCardResult(
                AddCardResult(
                    result = state,
                    redirect = redirect,
                ),
            )
        },
        onError = {
            onAddCardStatusChanged.onAddCardResult(
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
