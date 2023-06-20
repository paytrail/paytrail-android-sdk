package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object PaytrailSHA512HmacCalculator : PaytrailHmacCalculator("sha512", "HmacSHA512")
object PaytrailSHA256HmacCalculator : PaytrailHmacCalculator("sha256", "HmacSHA256")

/** Get calculator instance for calculating Paytrail request/response HMAC signatures. */
internal fun paytrailHmacCalculator(algorithmHeader: String): PaytrailHmacCalculator =
    when (algorithmHeader) {
        "sha256" -> PaytrailSHA256HmacCalculator
        "sha512" -> PaytrailSHA512HmacCalculator
        else -> throw IllegalArgumentException("No calculator for $algorithmHeader")
    }

abstract class PaytrailHmacCalculator(
    /** Value for `checkout-algorithm` header */
    val algorithmHeader: String,
    /** ID of the algorithm for [Mac] */
    private val macAlgorithm: String,
) {
    fun calculateHmac(request: Request, key: String): String {
        val headerList = request.headers.headersForHmacCalculation()
        val bodyString = request.bodyString
        return calculateHmac(
            message = (headerList + bodyString).joinToString("\n"),
            key = key
        )
    }

    fun calculateHmac(response: Response, key: String): String {
        val headerList = response.headers.headersForHmacCalculation()
        val bodyString = response.bodyString
        return calculateHmac(
            message = (headerList + bodyString).joinToString("\n"),
            key = key
        )
    }

    private fun calculateHmac(message: String, key: String): String =
        calculateHmac(
            message = message.toByteArray(),
            key = key.toByteArray(),
        ).bytesToHex()

    private fun calculateHmac(message: ByteArray?, key: ByteArray?): ByteArray =
        with(Mac.getInstance(macAlgorithm)) {
            init(SecretKeySpec(key, algorithm))
            doFinal(message)
        }
}

private val Response.bodyString: String
    get() {
        val source = body!!.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        return source.buffer.clone().readUtf8()
    }

private val Request.bodyString: String
    get() {
        val buffer = Buffer()
        body?.writeTo(buffer)
        return buffer.readUtf8()
    }

private fun Headers.headersForHmacCalculation(): List<String> =
    filter { it.first.startsWith("checkout-") }
        .sortedBy { it.first }
        .map { "${it.first}:${it.second}" }
        .toList()

private fun ByteArray.bytesToHex(): String {
    val hexArray = "0123456789abcdef".toCharArray()
    val hexChars = CharArray(size * 2)
    var v: Int
    for (j in indices) {
        v = this[j].toInt() and 0xFF
        hexChars[j * 2] = hexArray[v ushr 4]
        hexChars[j * 2 + 1] = hexArray[v and 0x0F]
    }
    return String(hexChars)
}
