package fi.paytrail.demo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fi.paytrail.demo.DemoDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        DemoDb::class.java,
        "paytrail-demo",
    ).build()

    @Provides
    @Singleton
    fun provideCardDao(db: DemoDb) = db.tokenizedCardDao()
}
