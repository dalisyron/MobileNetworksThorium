package com.example.usecase.interactor

import com.example.common.entity.Preference
import com.example.usecase.repository.PreferenceRepository
import javax.inject.Inject

class GetAllPreferencesUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(): List<Preference> {
        return preferenceRepository.getAllPreferences()
    }
}