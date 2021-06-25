package com.example.thorium.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager constructor(
    val context: Context
) {
    data class Preference(
        val title: String,
        val key: Preferences.Key<Int>,
        var value: Int
    )

    companion object {
        private const val TITLE_GENERATION_COLOR = "Generation Color"
        private const val TITLE_LOCATION_COLOR = "Location Color"
        private const val TITLE_MCC_COLOR = "MCC Color"
        private const val TITLE_MNC_COLOR = "MNC Color"
        private const val TITLE_STRENGTH_COLOR = "Strength Color"

        val KEY_GENERATION_COLOR = intPreferencesKey("GENERATION_COLOR")
        val KEY_LOCATION_COLOR = intPreferencesKey("LOCATION_COLOR")
        val KEY_MCC_COLOR = intPreferencesKey("MCC_COLOR")
        val KEY_MNC_COLOR = intPreferencesKey("MNC_COLOR")
        val KEY_STRENGTH_COLOR = intPreferencesKey("STRENGTH_COLOR")
    }


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun setGenerationColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_GENERATION_COLOR] = value
        }

    suspend fun setLocationColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_LOCATION_COLOR] = value
        }

    suspend fun setMCCColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_MCC_COLOR] = value
        }

    suspend fun setMNCColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_MNC_COLOR] = value
        }

    suspend fun setStrengthColor(value: Int) =
        context.dataStore.edit { preferences ->
            preferences[KEY_STRENGTH_COLOR] = value
        }

    fun getGenerationColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_GENERATION_COLOR] ?: -1
        }

    fun getLocationColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_LOCATION_COLOR] ?: -1
        }

    fun getMCCColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_MCC_COLOR] ?: -1
        }

    fun getMNCColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_MNC_COLOR] ?: -1
        }

    fun getStrengthColor(): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_STRENGTH_COLOR] ?: -1
        }

    suspend fun getAllPreferences(): List<Preference> {
        val allPreferences = ArrayList<Preference>()

        allPreferences.addAll(
            mutableListOf(
                Preference(
                    TITLE_GENERATION_COLOR,
                    KEY_GENERATION_COLOR,
                    getGenerationColor().first()
                ),
                Preference(
                    TITLE_LOCATION_COLOR,
                    KEY_LOCATION_COLOR,
                    getLocationColor().first()
                ),
                Preference(
                    TITLE_MCC_COLOR,
                    KEY_MCC_COLOR,
                    getMCCColor().first()
                ),
                Preference(
                    TITLE_MNC_COLOR,
                    KEY_MNC_COLOR,
                    getMNCColor().first()
                ),
                Preference(
                    TITLE_STRENGTH_COLOR,
                    KEY_STRENGTH_COLOR,
                    getStrengthColor().first()
                )
            )
        )

        if (allPreferences.isEmpty()) {
            addPreferencesForFirstTime(allPreferences)
        }

        return allPreferences
    }

    private suspend fun addPreferencesForFirstTime(allPreferences: ArrayList<Preference>) {
        setGenerationColor(ColorSpinnerItem.COLOR_BLUE)
        setLocationColor(ColorSpinnerItem.COLOR_YELLOW)
        setMCCColor(ColorSpinnerItem.COLOR_RED)
        setMNCColor(ColorSpinnerItem.COLOR_GREEN)
        setStrengthColor(ColorSpinnerItem.COLOR_CYAN)

        allPreferences.add(
            Preference(
                TITLE_GENERATION_COLOR,
                KEY_GENERATION_COLOR,
                ColorSpinnerItem.COLOR_BLUE
            )
        )

        allPreferences.add(
            Preference(
                TITLE_LOCATION_COLOR,
                KEY_LOCATION_COLOR,
                ColorSpinnerItem.COLOR_YELLOW
            )
        )

        allPreferences.add(
            Preference(
                TITLE_MCC_COLOR,
                KEY_MCC_COLOR,
                ColorSpinnerItem.COLOR_RED
            )
        )

        allPreferences.add(
            Preference(
                TITLE_MNC_COLOR,
                KEY_MNC_COLOR,
                ColorSpinnerItem.COLOR_GREEN
            )
        )

        allPreferences.add(
            Preference(
                TITLE_STRENGTH_COLOR,
                KEY_STRENGTH_COLOR,
                ColorSpinnerItem.COLOR_CYAN
            )
        )
    }
}