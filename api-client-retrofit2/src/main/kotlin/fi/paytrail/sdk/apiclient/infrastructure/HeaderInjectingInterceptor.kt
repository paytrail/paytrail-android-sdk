package fi.paytrail.sdk.apiclient.infrastructure

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
/**
 * An interceptor responsible for injecting a specific header into HTTP requests.
 *
 * Interceptors can be used to transform or inspect requests and responses in OkHttp. The `HeaderInjectingInterceptor`
 * is designed to conditionally add a header to requests if it's not already present.
 *
 * This is useful for cases where a default header value should be set for all requests, but can be overridden
 * by specific requests. For example, you might want to set a default "User-Agent" or "Authorization" header for
 * all requests but allow specific requests to provide a different value.
 *
 * @param header The name of the header to inject or inspect.
 * @param valueProvider A lambda function that provides the value for the header, given a request.
 *
 * @constructor Initializes the interceptor with the provided header name and its value provider.
 */
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
