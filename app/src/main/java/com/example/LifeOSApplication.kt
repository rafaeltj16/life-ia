package com.example

import android.app.Application
import com.example.core.database.AppDatabase
import com.google.firebase.FirebaseApp
import com.lifeos.core.database.LifeOSDatabase
import timber.log.Timber

open class LifeOSApplication : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val lifeDatabase: LifeOSDatabase by lazy {
        LifeOSDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for structured logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            Timber.e(e, "FirebaseApp initialization failed: ${e.message}")
        }
    }
}
