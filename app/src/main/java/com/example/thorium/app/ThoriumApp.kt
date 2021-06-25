package com.example.thorium.app

import android.app.Application
import android.content.Context
import com.example.data.datasource.DataStoreManager
import com.example.thorium.R
import com.example.thorium.database.MainDatabaseManager
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
            dataStoreManager.setDefaultPreferencesIfNeeded(
                R.color.red,
                R.color.blue,
                R.color.magenta
            )
        }
    }

    companion object {
        var applicationContext: Context? = null
    }
}