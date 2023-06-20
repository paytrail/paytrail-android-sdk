package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Response

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
