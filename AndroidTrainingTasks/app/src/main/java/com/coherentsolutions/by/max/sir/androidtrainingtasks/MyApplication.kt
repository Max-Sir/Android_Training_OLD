package com.coherentsolutions.by.max.sir.androidtrainingtasks

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.coherentsolutions.by.max.sir.androidtrainingtasks.database.UserDatabase
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.notifications.NotificationHelper
import com.coherentsolutions.by.max.sir.androidtrainingtasks.service.ServiceLocator
import com.coherentsolutions.by.max.sir.androidtrainingtasks.work.RefreshDataWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication : Application() {

    companion object {
        const val INFO_TAG = "MyApplication"


        val applicationJob by lazy {
            Job()
        }
        val uiScope by lazy {
            CoroutineScope(Dispatchers.Main + applicationJob)
        }
        val appScope by lazy {
            CoroutineScope(Dispatchers.Default)
        }
    }

    override fun onCreate() {
        val database = UserDatabase.getInstance(this)
        Timber.i("created application")
        ServiceLocator.context = this
        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel."
        )
        super.onCreate()

        delayedInit()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    private fun delayedInit() {
        appScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }


    override fun onTerminate() {
        super.onTerminate()
        applicationJob.cancel()
    }
}