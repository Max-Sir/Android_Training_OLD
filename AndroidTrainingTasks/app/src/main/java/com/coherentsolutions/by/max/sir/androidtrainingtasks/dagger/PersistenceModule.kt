package com.coherentsolutions.by.max.sir.androidtrainingtasks.dagger

import android.content.Context
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PersistenceModule {
    @Provides
    fun providePetPersistence(database: UserDatabase): PetPersistence =
        DefaultPetPersistence(database)

    @Provides
    fun providePetstorePersistence(@ApplicationContext context: Context): PetstorePersistence =
        SharedPrefPetstorePersistence(context)

    @Provides
    fun provideUserPersistence(database: UserDatabase): UserPersistence =
        DefaultUserPersistence(database)
}