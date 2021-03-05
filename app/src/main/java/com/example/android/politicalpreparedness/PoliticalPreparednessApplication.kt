package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber

/**
 * The Political Preparedness [Application] class.
 */
class PoliticalPreparednessApplication : Application() {

    /**
     * Invoked when the [Application] first starts up.
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}