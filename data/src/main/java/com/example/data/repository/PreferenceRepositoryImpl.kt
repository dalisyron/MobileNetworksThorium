package com.example.data.repository

import com.example.common.entity.Preference
import com.example.data.datasource.DataStoreManager
import com.example.usecase.repository.PreferenceRepository

class PreferenceRepositoryImpl constructor(
    private val dataStoreManager: DataStoreManager
) : PreferenceRepository {

    override suspend fun setPreference(preference: Preference) {
        dataStoreManager.setPreference(preference)
    }

    override suspend fun getAllPreferences(): List<Preference> {
        return dataStoreManager.getAllPreferences()
    }
}