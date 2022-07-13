package com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence

import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.User

interface UserPersistence {
    suspend fun add(userResponse: User)
    suspend fun get(username: String): User?
    suspend fun delete(username: String)
}