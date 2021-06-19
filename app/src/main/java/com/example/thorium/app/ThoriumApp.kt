package com.example.thorium.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ThoriumApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ThoriumApp.applicationContext = this
    }

    companion object {
        var applicationContext: Context? = null
    }
}