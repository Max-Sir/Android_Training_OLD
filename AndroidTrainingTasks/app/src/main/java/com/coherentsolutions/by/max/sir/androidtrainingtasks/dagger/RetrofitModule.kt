package com.coherentsolutions.by.max.sir.androidtrainingtasks.dagger

import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    fun provideRetrofit(): RetrofitService =
        PetstoreService.retrofit.create(RetrofitService::class.java)
}