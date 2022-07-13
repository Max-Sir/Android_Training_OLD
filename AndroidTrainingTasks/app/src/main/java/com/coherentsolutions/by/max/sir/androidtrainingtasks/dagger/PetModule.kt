package com.coherentsolutions.by.max.sir.androidtrainingtasks.dagger

import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pet.PetFragmentArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class PetModule {

    @Provides
    fun petResponse(args: PetFragmentArgs): PetResponse = args.pet
}