package fi.paytrail.demo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.paytrail.demo.SAMPLE_MERCHANT_ACCOUNT
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.PaytrailApiClient

@Module
@InstallIn(SingletonComponent::class)
class PaytrailApiModule {

    @Provides
    fun provideApiClient(): PaytrailApiClient = PaytrailApiClient(merchantAccount = SAMPLE_MERCHANT_ACCOUNT)

    @Provides
    fun provideTokenApi(apiClient: PaytrailApiClient): TokenPaymentsApi =
        apiClient.createService(TokenPaymentsApi::class.java)

    @Provides
    fun providePaymentsApi(apiClient: PaytrailApiClient): PaymentsApi =
        apiClient.createService(PaymentsApi::class.java)
}
