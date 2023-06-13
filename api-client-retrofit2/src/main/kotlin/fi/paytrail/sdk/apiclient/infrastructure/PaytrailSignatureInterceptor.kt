package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class PaytrailSignatureInterceptor(
    private val secretProvider: () -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("checkout-algorithm", "sha512")
            .build()

        // When calculating the signature for Paytrail API calls,
        // include only the "checkout-***" signatures sorted alphabetically
        val headerList = request.headers
            .filter { it.first.startsWith("checkout-") }
            .sortedBy { it.first }
            .map { "${it.first}:${it.second}" }
            .toList()

        val bodyString = request.bodyString()

        val messageForHmac = (headerList + bodyString).joinToString(separator = "\n")

        val signature = calculateSha512Hmac(
            message = messageForHmac,
            key = secretProvider.invoke(),
        )

        return chain.proceed(
            request.newBuilder()
                .addHeader("signature", signature)
                .build()
        )
    }

    private fun calculateSha512Hmac(message: String, key: String): String = try {
        calculateHmac(
            algorithm = "HmacSHA512",
            key = key.toByteArray(),
            message = message.toByteArray()
        ).bytesToHex()
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun calculateHmac(algorithm: String?, key: ByteArray?, message: ByteArray?): ByteArray =
        with(Mac.getInstance(algorithm)) {
            init(SecretKeySpec(key, algorithm))
            doFinal(message)
        }

    private fun  ByteArray.bytesToHex(): String {
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
}

private fun Request.bodyString(): String {
    val buffer = Buffer()
    body?.writeTo(buffer)
    return buffer.readUtf8()
}
