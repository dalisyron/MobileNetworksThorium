package com.example.thorium.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.common.entity.Preference
import com.example.common.entity.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager constructor(
    val context: Context
) {

    companion object {
        private const val TITLE_2G_COLOR = "2G Color"
        private const val TITLE_3G_COLOR = "3G Color"
        private const val TITLE_4G_COLOR = "4G Color"

        val KEY_2G_COLOR = intPreferencesKey("2G_COLOR")
        val KEY_3G_COLOR = intPreferencesKey("3G_COLOR")
        val KEY_4G_COLOR = intPreferencesKey("4G_COLOR")

        val keyList = listOf(
            KEY_2G_COLOR,
            KEY_3G_COLOR,
            KEY_4G_COLOR,
        )
    }

    private fun mapFrom(preferenceKey: PreferenceKey): Preferences.Key<Int> {
        return keyList[preferenceKey.value]
    }

    private fun mapTo(key: Preferences.Key<Int>): PreferenceKey {
        return PreferenceKey(keyList.indexOf(key))
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun set2GColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_2G_COLOR] = value
        }

    suspend fun set3GColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_3G_COLOR] = value
        }

    suspend fun set4GColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_4G_COLOR] = value
        }

    fun get2GColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_2G_COLOR]!!
        }

    fun get3GColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_3G_COLOR]!!
        }

    fun get4GColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_4G_COLOR]!!
        }

    suspend fun getAllPreferences(): List<Preference> {
        val allPreferences = ArrayList<Preference>()

        allPreferences.addAll(
            mutableListOf(
                createPreference(
                    TITLE_2G_COLOR,
                    KEY_2G_COLOR,
                    get2GColor().first()
                ),
                createPreference(
                    TITLE_3G_COLOR,
                    KEY_3G_COLOR,
                    get3GColor().first()
                ),
                createPreference(
                    TITLE_4G_COLOR,
                    KEY_4G_COLOR,
                    get4GColor().first()
                ),
            )
        )

        return allPreferences
    }


    private fun createPreference(title: String, key: Preferences.Key<Int>, color: Int): Preference {
        return Preference(
            title,
            mapTo(key),
            color
        )
    }

    suspend fun isPreferencesEmpty(): Boolean {
        return context.dataStore.data.map { preferences ->
            preferences
        }.first().asMap().keys.isEmpty()
    }
}