package com.coherentsolutions.by.max.sir.androidtrainingtasks.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetStatus
import com.coherentsolutions.by.max.sir.androidtrainingtasks.network.RetrofitService
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {


    companion object {
        const val WORK_NAME = "com.example.android.devbyteviewer.work.RefreshDataWorker"
    }

    @Inject
    lateinit var retrofitService: RetrofitService


    override suspend fun doWork(): Result {
        val database = UserDatabase.getInstance(applicationContext)
        val petList: List<PetResponse>
        /**
         * @note one of the ways to check even worker is works or not
         * Looper.prepare()
         * Toast.makeText(applicationContext,"Hello I'm Worker", Toast.LENGTH_LONG).show()
         */
        try {
            petList = retrofitService.getPetsByStatus(PetStatus.ALL.value).await()
        } catch (ex: HttpException) {
            Timber.i("Worker is Retrying")
            return Result.retry()
        }

        val dbPetList = database.petsDao.getAll()
        Timber.i("DB now is empty(true/false):  ${dbPetList.isEmpty()}")
        petList.forEach {
            if (!dbPetList.contains(it.toPet())) {
                database.petsDao.add(it.toPet())
            } else {
                Timber.i("There was one such element")
            }
        }
        return Result.success()
    }
}