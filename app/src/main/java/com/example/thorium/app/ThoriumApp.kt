package com.example.thorium.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class ThoriumApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ThoriumApp.applicationContext = this
    }
    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        var applicationContext: Context? = null
    }
}