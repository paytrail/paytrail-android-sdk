package fi.paytrail.sdk.apiclient.infrastructure

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.infrastructure.Serializer.kotlinxSerializationJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient(
    private var baseUrl: String = "https://services.paytrail.com",
    private val okHttpClientBuilder: OkHttpClient.Builder? = null,
    private val merchantAccount: MerchantAccount,
) {
    var logger: ((String) -> Unit)? = null

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(kotlinxSerializationJson.asConverterFactory("application/json".toMediaType()))
    }

    private val clientBuilder: OkHttpClient.Builder by lazy {
        okHttpClientBuilder ?: defaultClientBuilder
    }

    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor { message -> logger?.invoke(message) }
                    .apply { level = HttpLoggingInterceptor.Level.BODY },
            )
    }

    init {
        normalizeBaseUrl()
    }

    fun setLogger(logger: (String) -> Unit): ApiClient {
        this.logger = logger
        return this
    }

    fun <S> createService(serviceClass: Class<S>): S {
        val interceptors = clientBuilder.interceptors()
        if (interceptors.none { it is PaytrailSignatureInterceptor }) {
            interceptors.addAll(
                index = 0,
                elements = listOf(
                    PaytrailCallMethodInterceptor(),
                    PaytrailCallTimestampInterceptor(),
                    PaytrailNonceInterceptor(),
                    PaytrailAccountIdInterceptor { merchantAccount.id },
                    PaytrailSignatureInterceptor { merchantAccount.secret },
                ),
            )
        }

        return retrofitBuilder.callFactory(clientBuilder.build()).build().create(serviceClass)
    }

    private fun normalizeBaseUrl() {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
    }

    private inline fun <T, reified U> Iterable<T>.runOnFirst(callback: U.() -> Unit) {
        for (element in this) {
            if (element is U) {
                callback.invoke(element)
                break
            }
        }
    }
}
