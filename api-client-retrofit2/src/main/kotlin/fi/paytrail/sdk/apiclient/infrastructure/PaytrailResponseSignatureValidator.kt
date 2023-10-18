package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
/**
 * Interceptor for validating the signature of HTTP responses received from the Paytrail API.
 *
 * Security is paramount in payment transactions, and the integrity of API responses must be guaranteed.
 * The `PaytrailResponseSignatureValidator` acts as a guard to ensure that the responses received from the
 * Paytrail API are genuine and haven't been tampered with during transmission.
 *
 * This interceptor checks the HMAC signature attached to each response against a locally generated signature
 * using the merchant's secret. If the signatures match, the response is considered authentic. Otherwise, an
 * exception is thrown, signaling potential data tampering or a man-in-the-middle attack.
 *
 * @param calculatorProvider A lambda function that, given an algorithm, returns the appropriate HMAC calculator.
 * @param secretProvider A lambda function that returns the merchant's secret key, used for validating the response signature.
 *
 * @constructor Creates an instance of the validator with the specified HMAC calculator provider and secret provider.
 */
class PaytrailResponseSignatureValidator(
    private val calculatorProvider: (String) -> PaytrailHmacCalculator,
    private val secretProvider: () -> String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) checkResponseSignature(response)
        return response
    }

    private fun checkResponseSignature(response: Response) {
        val algorithm = response.headers["checkout-algorithm"]
        if (algorithm.isNullOrEmpty()) throw RuntimeException("Missing checkout-algorithm")

        val signature = response.headers["signature"]
        if (signature.isNullOrEmpty()) throw RuntimeException("Missing signature")

        val calculator = calculatorProvider.invoke(algorithm)
        val expectedSignature = calculator.calculateHmac(response, secretProvider.invoke())

        if (expectedSignature != signature) {
            throw InvalidSignatureException(
                "Invalid response signature\n" +
                    " !!! Actual:   $signature\n" +
                    " !!! Expected: $expectedSignature",
            )
        }
    }
}
