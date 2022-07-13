package com.coherentsolutions.by.max.sir.androidtrainingtasks.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.User

@Dao
interface UserDao {
    @Insert
    fun add(userResponse: User)

    @Query("SELECT * FROM users_table WHERE username=:username")
    fun get(username: String): User?

    @Query("DELETE FROM users_table WHERE username=:username")
    fun delete(username: String)
}