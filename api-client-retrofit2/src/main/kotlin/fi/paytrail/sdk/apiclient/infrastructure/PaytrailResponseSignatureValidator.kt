package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Response

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

        if (expectedSignature != signature) throw RuntimeException("Invalid response signature")
    }
}
