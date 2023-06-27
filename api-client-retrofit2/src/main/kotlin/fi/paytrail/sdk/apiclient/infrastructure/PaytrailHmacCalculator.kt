package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.net.URL
import java.net.URLDecoder
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UnknownHmacAlgorithmException(message: String) : Exception(message)
class InvalidSignatureException(message: String) : Exception(message)

class PaytrailHmacCalculator private constructor(
    /** Value for `checkout-algorithm` header */
    val algorithmHeader: String,
    /** ID of the algorithm for [Mac] */
    private val macAlgorithm: String,
) {

    companion object {
        const val ALGORITHM_SHA256 = "sha256"
        const val ALGORITHM_SHA512 = "sha512"

        val SHA256 = PaytrailHmacCalculator(ALGORITHM_SHA256, "HmacSHA256")
        val SHA512 = PaytrailHmacCalculator(ALGORITHM_SHA512, "HmacSHA512")

        /** Get calculator instance for calculating Paytrail request/response HMAC signatures. */
        fun getCalculator(algorithm: String): PaytrailHmacCalculator =
            when (algorithm) {
                ALGORITHM_SHA256 -> SHA256
                ALGORITHM_SHA512 -> SHA512
                else -> throw IllegalArgumentException("No known HMAC calculator for $algorithm")
            }

        /**
         * Calculate a Paytrail Hmac signature for a given URL. This can be used to validate the HMAC
         * signature of Paytrail HTTP redirect requests. The HMAC algorithm for calculation is
         * selected based on query parameter `checkout-algorithm`. Supported values are
         * [ALGORITHM_SHA256] and [ALGORITHM_SHA512]
         *
         * @param url Encoded string of the url to calculate HMAC for.
         * @throws IllegalArgumentException if [url] does not contain `checkout-algorithm`
         * query parameters.
         */
        @Suppress("MemberVisibilityCanBePrivate")
        fun calculateHmac(url: String, key: String) = calculateHmac(url = URL(url), key = key)

        /**
         * Calculate a Paytrail Hmac signature for a given URL. This can be used to validate the HMAC
         * signature of Paytrail HTTP redirect requests. The HMAC algorithm for calculation is
         * selected based on query parameter `checkout-algorithm`. Supported values are
         * [ALGORITHM_SHA256] and [ALGORITHM_SHA512]
         *
         * @param url url to calculate HMAC for.
         * @throws IllegalArgumentException if [url] does not contain `checkout-algorithm`
         * query parameters.
         */
        @Suppress("MemberVisibilityCanBePrivate")
        fun calculateHmac(url: URL, key: String): String {
            val parameters = url.parameters()
            val algorithm = url.parameter("checkout-algorithm")
            if (algorithm.isNullOrEmpty()) throw IllegalArgumentException("No checkout-algorithm parameter found")
            val calculator = getCalculator(algorithm)
            return calculator.calculateHmac(params = parameters, key = key)
        }

        /**
         * Verify validity of Paytrail redirect URL by checking its `signature` query parameter
         * against calculated HMAC.
         *
         * @return true if redirect URL has been signed with corresponding key
         * @throws IllegalArgumentException if [url] does not contain `signature` or `checkout-algorithm` query parameters
         * @throws UnknownHmacAlgorithmException if [url] requires verifying signature with unsupported algorithm
         */
        fun verifyUrlSignature(url: String, key: String): Boolean =
            verifyUrlSignature(url = URL(url), key = key)

        /**
         * Verify validity of Paytrail redirect URL by checking its `signature` query parameter
         * against calculated HMAC.
         *
         * @throws IllegalArgumentException if [url] does not contain `signature` or `checkout-algorithm` query parameters
         * @throws UnknownHmacAlgorithmException if [url] requires verifying signature with unsupported algorithm
         */
        fun verifyUrlSignature(url: URL, key: String): Boolean {
            val requestSignature = url.parameter("signature")
            return calculateHmac(url, key) == requestSignature
        }
    }

    fun calculateHmac(request: Request, key: String): String = calculateHmac(
        params = request.headers,
        body = request.bodyString,
        key = key,
    )

    fun calculateHmac(response: Response, key: String): String = calculateHmac(
        params = response.headers,
        body = response.bodyString,
        key = key,
    )

    fun calculateHmac(
        params: Iterable<Pair<String, String>> = emptySet(),
        body: String = "",
        key: String,
    ): String = calculateHmac(
        message = (params.asHmacCalculationString() + body).joinToString("\n"),
        key = key,
    )

    private fun calculateHmac(message: String, key: String): String = calculateHmac(
        message = message.toByteArray(),
        key = key.toByteArray(),
    ).joinToString(separator = "", transform = { "%02x".format(it) })

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

private fun Iterable<Pair<String, String>>.asHmacCalculationString(): List<String> =
    filter { it.first.startsWith("checkout-") }
        .sortedBy { it.first }
        .map { "${it.first}:${it.second}" }
        .toList()

private fun URL.parameters(): Iterable<Pair<String, String>> {
    return query.split('&').map {
        val parts = it.split('=', limit = 2)
        val name = parts.firstOrNull() ?: ""
        val value = URLDecoder.decode(parts.drop(1).firstOrNull() ?: "", "UTF-8")
        name to value
    }
}

private fun URL.parameter(parameterName: String): String? {
    return parameters().firstOrNull { it.first == parameterName }?.second
}
