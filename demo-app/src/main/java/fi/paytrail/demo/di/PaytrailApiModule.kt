package fi.paytrail.demo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fi.paytrail.sdk.apiclient.apis.PaymentsApi
import fi.paytrail.sdk.apiclient.apis.TokenPaymentsApi
import fi.paytrail.sdk.apiclient.infrastructure.ApiClient

@Module
@InstallIn(SingletonComponent::class)
class PaytrailApiModule {

    @Provides
    fun provideApiClient(): ApiClient = ApiClient()

    @Provides
    fun provideTokenApi(apiClient: ApiClient): TokenPaymentsApi =
        apiClient.createService(TokenPaymentsApi::class.java)

    @Provides
    fun providePaymentsApi(apiClient: ApiClient): PaymentsApi =
        apiClient.createService(PaymentsApi::class.java)
}
