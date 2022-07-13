package com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence

import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Pet

interface PetPersistence {

    suspend fun add(pet: Pet)

    suspend fun get(id: Long): Pet

    suspend fun delete(id: Long)

    suspend fun clearAll()

    suspend fun getAll(): List<Pet>
}