package com.example.thorium.app

import android.app.Application
import android.content.Context
import com.example.thorium.R
import com.example.thorium.database.MainDatabaseManager
import com.example.thorium.datasource.DataStoreManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class ThoriumApp : Application() {

    @Inject
    lateinit var mainDatabaseManager: MainDatabaseManager

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onCreate() {
        super.onCreate()
        ThoriumApp.applicationContext = this
        mainDatabaseManager.onStart()

        runBlocking {
            if (dataStoreManager.isPreferencesEmpty()) {
                setDefaultPreferences()
            }
        }
    }

    private suspend fun setDefaultPreferences() {
        dataStoreManager.set2GColor(R.color.blue)
        dataStoreManager.set3GColor(R.color.red)
        dataStoreManager.set4GColor(R.color.green)
    }

    companion object {
        var applicationContext: Context? = null
    }
}