package com.example.thorium.app

import android.app.Application
import android.content.Context
import com.example.thorium.database.MainDatabaseManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ThoriumApp : Application() {

    @Inject
    lateinit var mainDatabaseManager: MainDatabaseManager

    override fun onCreate() {
        super.onCreate()
        ThoriumApp.applicationContext = this
        mainDatabaseManager.onStart()
    }

    companion object {
        var applicationContext: Context? = null
    }
}