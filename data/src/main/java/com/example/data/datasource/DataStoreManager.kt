package com.example.data.datasource

import com.example.common.entity.Preference

interface DataStoreManager {
    suspend fun setPreference(preference: Preference)

    suspend fun getAllPreferences(): List<Preference>
}