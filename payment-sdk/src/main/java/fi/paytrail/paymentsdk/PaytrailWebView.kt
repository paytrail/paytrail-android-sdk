package fi.paytrail.paymentsdk

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import fi.paytrail.sdk.apiclient.infrastructure.InvalidSignatureException
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailHmacCalculator.Companion.verifyUrlSignature
import fi.paytrail.sdk.apiclient.models.Callbacks
import java.net.URLEncoder

enum class PaytrailWebViewCallMethod { GET, POST }

/**
 * Base WebView.
 *
 * The `PaytrailWebView` Composable provides a seamless integration for loading the Paytrail payment gateway
 * within a Compose application. This WebView is designed to handle the different scenarios of Paytrail's
 * payment flow, including success, cancellation, errors, and form resubmissions.
 *
 * It also supports both POST and GET methods for initiating the payment, and is equipped with built-in security
 * measures like signature verification for the redirect URLs.
 *
 * @param modifier The modifier to be applied to the WebView for adjusting layout behavior.
 * @param url The initial URL to load within the WebView.
 * @param method The HTTP method to use. Defaults to GET. Use POST when you need to pass parameters in the request body.
 * @param postParameters The parameters to be sent in the request body if the method is POST. Ignored if method is GET.
 * @param redirectUrls Contains the success and cancellation URLs to detect the end of the payment flow.
 * @param signatureVerificationSecret The merchant's secret key to validate the signature of redirect URLs.
 * @param onFinalRedirect Callback triggered once the payment gateway redirects to either the success or cancel URL.
 * @param onError Callback triggered when there's an error, such as an invalid URL signature.
 * @param allowBackNavigation Whether the WebView should handle back navigation. Defaults to true.
 */
@Composable
fun PaytrailWebView(
    modifier: Modifier = Modifier,
    url: String,
    method: PaytrailWebViewCallMethod = PaytrailWebViewCallMethod.GET,
    postParameters: Iterable<Pair<String, String>> = emptyList(),
    redirectUrls: Callbacks,
    signatureVerificationSecret: String,
    onFinalRedirect: (Uri) -> Unit,
    onError: (Exception) -> Unit,
    allowBackNavigation: Boolean = true,
) {
    var webView: WebView? = null

    var canGoBack by remember { mutableStateOf(false) }
    val webViewState: State<Bundle> = rememberSaveable { mutableStateOf(Bundle()) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        PaytrailLogger.i("onPageStarted ::: $url")
                        canGoBack = view?.canGoBack() ?: false
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onLoadResource(view: WebView?, url: String?) {
                        PaytrailLogger.v("onLoadResource ::: $url")
                        super.onLoadResource(view, url)
                    }
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?,
                    ) {
                        PaytrailLogger.w(
                            "onReceivedHttpError ::: ${request?.method} ${request?.url} ::: $error",
                        )
                        super.onReceivedError(view, request, error)
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?,
                    ) {
                        PaytrailLogger.w(
                            "onReceivedHttpError ::: ${request?.method} ${request?.url} ::: ${errorResponse?.statusCode}",
                        )
                        super.onReceivedHttpError(view, request, errorResponse)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        PaytrailLogger.i("onPageFinished $url")
                        canGoBack = view?.canGoBack() ?: false
                        super.onPageFinished(view, url)
                    }

                    override fun onFormResubmission(
                        view: WebView?,
                        dontResend: Message?,
                        resend: Message?,
                    ) {
                        PaytrailLogger.i("onFormResubmission")
                        super.onFormResubmission(view, dontResend, resend)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        PaytrailLogger.i(
                            "shouldOverrideUrlLoading ::: ${request.method} ${request.url}",
                        )
                        val (requestHost, requestPath) = with(request.url) { host to path }
                        val (successHost, successPath) = with(redirectUrls.success.toUri()) { host to path }
                        val (cancelHost, cancelPath) = with(redirectUrls.cancel.toUri()) { host to path }

                        if ((requestHost == successHost && requestPath == successPath) ||
                            (requestHost == cancelHost && requestPath == cancelPath)
                        ) {
                            if (verifyUrlSignature(
                                    url = request.url.toString(),
                                    key = signatureVerificationSecret,
                                )
                            ) {
                                onFinalRedirect(request.url)
                            } else {
                                onError(
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
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setInitialScale(90)
                }

                if (!webViewState.value.isEmpty) {
                    restoreState(webViewState.value)
                }

                webView = this
            }
        },
        update = {
            PaytrailLogger.i("AndroidView::update ::: webview.url=${it.url}")
            if (it.url == null) {
                when (method) {
                    PaytrailWebViewCallMethod.POST -> it.postUrl(
                        url,
                        postParameters.joinToFormBodyString().toByteArray(),
                    )

                    PaytrailWebViewCallMethod.GET -> it.loadUrl(url)
                }
            }
            webView = it
        },
    )

    BackHandler(enabled = allowBackNavigation && canGoBack) {
        webView?.apply {
            if (canGoBack()) {
                goBack()
            }
        }
    }
}

private fun Iterable<Pair<String, String>>.joinToFormBodyString(): String =
    joinToString(separator = "&") {
        val name = URLEncoder.encode(it.first, "UTF-8")
        val value = URLEncoder.encode(it.second, "UTF-8")
        "$name=$value"
    }
