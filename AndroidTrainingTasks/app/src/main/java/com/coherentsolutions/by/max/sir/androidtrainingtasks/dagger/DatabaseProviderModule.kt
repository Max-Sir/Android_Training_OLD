package com.coherentsolutions.by.max.sir.androidtrainingtasks.dagger

import android.content.Context
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class DatabaseProviderModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): UserDatabase =
        UserDatabase.getInstance(context)
}