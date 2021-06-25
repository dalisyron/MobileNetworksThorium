package com.example.usecase.interactor

import com.example.common.entity.Preference
import com.example.usecase.repository.PreferenceRepository
import javax.inject.Inject

class SetPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(preference: Preference) {
        preferenceRepository.setPreference(preference)
    }
}