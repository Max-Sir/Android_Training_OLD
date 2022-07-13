package com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence

import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.Pet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultPetPersistence @Inject constructor(dataSource: UserDatabase) : PetPersistence {

    val database = dataSource.petsDao

    override suspend fun add(pet: Pet) {
        withContext(Dispatchers.IO) {
            database.add(pet)
        }
    }

    override suspend fun get(id: Long): Pet {
        return withContext(Dispatchers.IO) {
            database.get(id)

        }

    }

    override suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            database.delete(id)
        }
    }

    override suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            database.clearAll()
        }
    }

    override suspend fun getAll(): List<Pet> {
        return withContext(Dispatchers.IO) {
            database.getAll()
        }
    }
}