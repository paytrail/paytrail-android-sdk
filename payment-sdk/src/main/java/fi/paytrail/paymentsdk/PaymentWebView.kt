package fi.paytrail.paymentsdk

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import fi.paytrail.paymentsdk.model.PaymentMethod
import fi.paytrail.paymentsdk.model.PaytrailPaymentRedirect
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.InvalidSignatureException
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailHmacCalculator.Companion.verifyUrlSignature
import fi.paytrail.sdk.apiclient.models.PaymentRequest

@Composable
fun PaymentWebView(
    viewModel: PaymentViewModel,
) {
    val paymentMethod: PaymentMethod? = viewModel.selectedPaymentProvider.observeAsState(null).value

    if (paymentMethod != null) {
        PaymentWebView(
            paymentMethod = paymentMethod,
            paymentRequest = viewModel.paymentRequest,
            merchantAccount = viewModel.merchantAccount,
            onPaymentRedirect = viewModel::onPaymentRedirect,
            onPaymentError = viewModel::onPaymentError,
        )
    }
}

@Composable
fun PaymentWebView(
    paymentMethod: PaymentMethod,
    paymentRequest: PaymentRequest,
    merchantAccount: MerchantAccount,
    onPaymentRedirect: (PaytrailPaymentRedirect) -> Unit,
    onPaymentError: (Exception) -> Unit,
) {
    var webView: WebView? = null

    var canGoBack by remember { mutableStateOf(false) }

    // TODO: Figure out how to persist/restore the WebView state through
    //       Activity (and process) destruction/recreation.
    //       Check how Accompanist WebView does this for inspiration.
    val webViewState: State<Bundle> = rememberSaveable { mutableStateOf(Bundle()) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        canGoBack = view?.canGoBack() ?: false
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        canGoBack = view?.canGoBack() ?: false
                        super.onPageFinished(view, url)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        val (requestHost, requestPath) = with(request.url) { host to path }
                        val (successHost, successPath) = with(paymentRequest.redirectUrls.success.toUri()) { host to path }
                        val (cancelHost, cancelPath) = with(paymentRequest.redirectUrls.cancel.toUri()) { host to path }

                        if ((requestHost == successHost && requestPath == successPath) ||
                            (requestHost == cancelHost && requestPath == cancelPath)
                        ) {
                            if (verifyUrlSignature(
                                    url = request.url.toString(),
                                    key = merchantAccount.secret,
                                )
                            ) {
                                onPaymentRedirect(PaytrailPaymentRedirect(request.url))
                            } else {
                                onPaymentError(
                                    InvalidSignatureException(
                                        "Invalid signature in redirect URL: ${request.url}",
                                    ),
                                )
                            }
                            return true
                        }

                        return false
                    }
                }

                with(settings) {
                    @SuppressLint("SetJavaScriptEnabled")
                    javaScriptEnabled = true

                    // We might need to support multiple windows during payment flows. If so,
                    // enable this and implement support for managing the windows.
                    setSupportMultipleWindows(false)
                    javaScriptCanOpenWindowsAutomatically = false

                    domStorageEnabled = true

                    builtInZoomControls = true
                    displayZoomControls = false
                }

                if (!webViewState.value.isEmpty) {
                    restoreState(webViewState.value)
                }

                webView = this
            }
        },
        update = {
            it.postUrl(
                paymentMethod.provider.url,
                paymentMethod.formParameters.toByteArray(),
            )

            webView = it
        },
    )

    BackHandler(enabled = canGoBack) {
        webView?.apply {
            if (canGoBack()) {
                goBack()
            }
        }
    }
}
