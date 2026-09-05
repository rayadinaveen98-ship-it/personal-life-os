package com.navin.personallifeos.di

import android.content.Context
import androidx.room.Room
import com.navin.personallifeos.data.local.AppDao
import com.navin.personallifeos.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "personal-life-os.db",
        ).build()

    @Provides
    fun provideAppDao(database: AppDatabase): AppDao = database.appDao()
}
