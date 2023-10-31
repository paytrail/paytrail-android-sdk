package fi.paytrail.sdk.apiclient.infrastructure

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import fi.paytrail.sdk.apiclient.MerchantAccount
import fi.paytrail.sdk.apiclient.PaytrailBaseOkHttpClient
import fi.paytrail.sdk.apiclient.infrastructure.Serializer.kotlinxSerializationJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
/**
 * A client to manage API requests to the Paytrail service.
 *
 * This class is responsible for building the Retrofit client with appropriate configurations,
 * signing the API requests with necessary headers and payload, and validating responses.
 *
 * @param baseUrl The base URL of the Paytrail service. Defaults to the Paytrail production URL.
 * @param okHttpClientBuilder An optional custom OkHttpClient builder. If not provided, it defaults to the base client defined in [PaytrailBaseOkHttpClient].
 * @param merchantAccount A data class representing merchant's account details in the Paytrail system.
 */
class PaytrailApiClient(
    private var baseUrl: String = "https://services.paytrail.com",
    private val okHttpClientBuilder: OkHttpClient.Builder? = PaytrailBaseOkHttpClient.baseClient?.newBuilder(),
    private val merchantAccount: MerchantAccount,
) {
    // Retrofit builder with appropriate configurations.
    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(kotlinxSerializationJson.asConverterFactory("application/json".toMediaType()))
    }

    // Client builder. Uses the provided OkHttpClient builder or defaults to a new client builder.
    private val clientBuilder: OkHttpClient.Builder by lazy {
        okHttpClientBuilder ?: defaultClientBuilder
    }

    // Default OkHttpClient builder.
    private val defaultClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient().newBuilder()
    }

    init {
        normalizeBaseUrl()
    }

    /**
     * Creates a Retrofit service instance for the given service class.
     *
     * @param serviceClass The Retrofit service class.
     * @return An instance of the specified service class.
     */
    fun <S> createService(serviceClass: Class<S>): S {
        val interceptors = clientBuilder.interceptors()
        if (interceptors.none { it is PaytrailRequestSigner }) {
            interceptors.addAll(
                index = 0,
                elements = listOf(
                    PaytrailCallMethodInjector(),
                    PaytrailCallTimestampInjector(),
                    PaytrailNonceInjector(),
                    PaytrailAccountIdInjector { merchantAccount.id },
                    PaytrailRequestSigner(
                        hmacCalculator = PaytrailHmacCalculator.SHA512,
                        secretProvider = { merchantAccount.secret },
                    ),
                    PaytrailResponseSignatureValidator(
                        calculatorProvider = PaytrailHmacCalculator::getCalculator,
                        secretProvider = { merchantAccount.secret },
                    ),
                ),
            )
        }

        return retrofitBuilder.callFactory(clientBuilder.build()).build().create(serviceClass)
    }

    /**
     * Normalizes the base URL by ensuring it ends with a slash.
     */
    private fun normalizeBaseUrl() {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
    }
}
