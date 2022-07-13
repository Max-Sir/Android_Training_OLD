package com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence

import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserPersistence @Inject constructor(dataSource: UserDatabase) : UserPersistence {

    val database = dataSource.userDao

    override suspend fun add(userResponse: User) {
        withContext(Dispatchers.IO) {
            database.add(userResponse)
        }
    }

    override suspend fun get(username: String): User? {
        return withContext(Dispatchers.IO) {
            database.get(username)
        }
    }

    override suspend fun delete(username: String) {
        withContext(Dispatchers.IO) {
            database.delete(username)
        }
    }
}