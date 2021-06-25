package com.example.usecase.repository

import com.example.common.entity.Preference

interface PreferenceRepository {
    suspend fun setPreference(preference: Preference)
    suspend fun getAllPreferences() :List<Preference>
}