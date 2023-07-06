package fi.paytrail.demo.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import fi.paytrail.demo.NAV_ARG_PAYMENT_ID
import java.util.UUID
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ViewModelParamTransactionId

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelParameters {
    @Provides
    @ViewModelParamTransactionId
    @ViewModelScoped
    fun providePaymentIdParameter(savedStateHandle: SavedStateHandle): UUID =
        UUID.fromString(savedStateHandle.getOrThrow(NAV_ARG_PAYMENT_ID))

    private fun <T> SavedStateHandle.getOrThrow(param: String): T =
        get<T>(param) ?: throw IllegalArgumentException("Could not get the '$param' parameter")
}
