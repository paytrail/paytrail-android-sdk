package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
/**
 * Interceptor for signing HTTP requests sent to the Paytrail API.
 *
 * The `PaytrailRequestSigner` is a crucial component in ensuring the security and authenticity of the requests
 * sent to the Paytrail API. This class intercepts every outgoing request, calculates the HMAC signature based
 * on the request content and the merchant's secret, and then attaches the signature to the request header.
 *
 * By doing so, it ensures that the API server can verify the authenticity of the request, confirming that
 * it's coming from a trusted source (the merchant).
 *
 * @param hmacCalculator The calculator responsible for generating the HMAC signature based on the provided algorithm.
 * @param secretProvider A lambda function that returns the merchant's secret key, used for signing the request.
 *
 * @constructor Creates an instance of the signer with the specified HMAC calculator and secret provider.
 */
class PaytrailRequestSigner(
    private val hmacCalculator: PaytrailHmacCalculator,
    private val secretProvider: () -> String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("checkout-algorithm", hmacCalculator.algorithmHeader)
            .build()

        val signature = hmacCalculator.calculateHmac(
            request = request,
            key = secretProvider.invoke(),
        )

        return chain.proceed(
            request.newBuilder()
                .addHeader("signature", signature)
                .build(),
        )
    }
}
