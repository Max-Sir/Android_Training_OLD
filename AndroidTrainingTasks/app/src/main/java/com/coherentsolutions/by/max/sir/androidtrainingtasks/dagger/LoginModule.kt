package com.coherentsolutions.by.max.sir.androidtrainingtasks.dagger

import com.coherentsolutions.by.max.sir.androidtrainingtasks.data.LoginDataSource
import com.coherentsolutions.by.max.sir.androidtrainingtasks.data.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

class LoginModule {
    @Provides
    fun loginRepository(): LoginRepository = LoginRepository(
        dataSource = LoginDataSource()
    )
}
