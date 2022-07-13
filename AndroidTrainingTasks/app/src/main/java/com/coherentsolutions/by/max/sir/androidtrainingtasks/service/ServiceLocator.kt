package com.coherentsolutions.by.max.sir.androidtrainingtasks.service

import android.annotation.SuppressLint
import android.content.Context
import com.coherentsolutions.by.max.sir.androidtrainingtasks.MyApplication.Companion.INFO_TAG
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.PetstoreService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import com.coherentsolutions.by.max.sir.androidtrainingtasks.persistence.*
import timber.log.Timber
import kotlin.reflect.KClass

@SuppressLint("StaticFieldLeak")
@Suppress("UNCHECKED_CAST")
object ServiceLocator {


    var context: Context? = null

    var database: UserDatabase? = null

    fun <T> getService(service: KClass<*>): T {
        return when (service) {
            RetrofitService::class -> PetstoreService.retrofit.create(service.java) as T
            else -> {
                throw Exception("wrong service $service")
            }
        }
    }

    fun <T> getPersistence(service: KClass<*>): T {
        Timber.tag(INFO_TAG).i("GET PERSISTENCE CALL")

        return when (service) {
            PetPersistence::class -> DefaultPetPersistence(database!!) as T //for other manipulations (f.ex. Shared Preferences for whole app)
            PetstorePersistence::class -> SharedPrefPetstorePersistence(context!!) as T // for DB!?!?
            UserPersistence::class -> DefaultUserPersistence(database!!) as T // for DB
            else -> {
                Timber.tag(INFO_TAG).i("BAD PERSISTENCE - NOT CREATED")
                throw Exception("wrong persistence $service")
            }
        }
    }
}


inline fun <reified T> service(): T {
    Timber.tag(INFO_TAG).i("SERVICE FUN")
    return ServiceLocator.getService(T::class)
}

inline fun <reified T> persistence(): T {
    Timber.tag(INFO_TAG).i("PERSISTENCE FUN CALL")

    return ServiceLocator.getPersistence(T::class)
}
