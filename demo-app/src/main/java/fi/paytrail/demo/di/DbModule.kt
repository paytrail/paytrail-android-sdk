package fi.paytrail.demo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fi.paytrail.demo.tokenization.TokenizedCardDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun provideCardDb(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        TokenizedCardDb::class.java,
        "tokenized-cards",
    ).build()

    @Provides
    @Singleton
    fun provideCardDao(db: TokenizedCardDb) = db.tokenizedCardDao()
}
