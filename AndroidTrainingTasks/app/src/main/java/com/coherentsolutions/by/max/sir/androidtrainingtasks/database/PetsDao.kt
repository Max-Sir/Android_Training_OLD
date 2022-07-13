package com.coherentsolutions.by.max.sir.androidtrainingtasks.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Pet

@Dao
interface PetsDao {
    @Insert
    fun add(petResponse: Pet)

    //limit 1 only cause that api swagger is opensource
    @Query(value = "SELECT * FROM pets_table WHERE id=:id LIMIT 1")
    fun get(id: Long): Pet

    @Query(value = "DELETE FROM pets_table WHERE id=:id")
    fun delete(id: Long)

    @Query(value = "DELETE FROM pets_table")
    fun clearAll()

    @Query(value = "SELECT * FROM pets_table ORDER BY name ASC")
    fun getAll(): List<Pet>
}
