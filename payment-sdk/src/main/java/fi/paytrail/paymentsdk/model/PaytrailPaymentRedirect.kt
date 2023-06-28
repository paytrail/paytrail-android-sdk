package fi.paytrail.paymentsdk.model

import android.net.Uri

class PaytrailPaymentRedirect(url: Uri) {
    val account: Int by lazy { url.getQueryParameter("checkout-account")!!.toInt() }
    val algorithm: String by lazy { url.getQueryParameter("checkout-algorithm")!! }
    val amount: Int by lazy { url.getQueryParameter("checkout-amount")!!.toInt() }
    val settlementReference: String? by lazy { url.getQueryParameter("checkout-settlement-reference") }
    val stamp: String by lazy { url.getQueryParameter("checkout-stamp")!! }
    val reference: String by lazy { url.getQueryParameter("checkout-reference")!! }
    val transactionId: String by lazy { url.getQueryParameter("checkout-transaction-id")!! }
    val status: Status by lazy { Status.fromQueryParamString(url.getQueryParameter("checkout-status")!!) }
    val provider: String by lazy { url.getQueryParameter("checkout-provider")!! }
    val signature: String by lazy { url.getQueryParameter("signature")!! }

    enum class Status(val s: String) {
        New("new"),
        Ok("ok"),
        Fail("fail"),
        Pending("pending"),
        Delayed("delayed"),
        ;

        companion object {
            fun fromQueryParamString(s: String): Status = values().first { it.s == s }
        }
    }
}
