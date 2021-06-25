package com.example.usecase.repository

import com.example.common.entity.Preference

interface PreferenceRepository {
    suspend fun setPreference(preference: Preference)
    suspend fun getAllPreferences() :List<Preference>
    suspend fun setDefaultPreferencesIfNeeded(
        g2Color: Int,
        g3Color: Int,
        g4Color: Int,
    )
}