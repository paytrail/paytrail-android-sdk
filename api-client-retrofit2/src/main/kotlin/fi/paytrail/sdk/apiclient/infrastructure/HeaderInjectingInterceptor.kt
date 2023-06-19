package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

open class HeaderInjectingInterceptor(private val header: String, private val valueProvider: (Request) -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (request.header(header).isNullOrEmpty()) {
            chain.proceed(
                chain.request().newBuilder()
                    .header(header, valueProvider.invoke(request))
                    .build(),
            )
        } else {
            chain.proceed(chain.request())
        }
    }
}
