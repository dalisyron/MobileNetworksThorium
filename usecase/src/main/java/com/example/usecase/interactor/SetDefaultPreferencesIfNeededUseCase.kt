package com.example.usecase.interactor

import com.example.usecase.repository.PreferenceRepository
import javax.inject.Inject

class SetDefaultPreferencesIfNeededUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(
        g2Color: Int,
        g3Color: Int,
        g4Color: Int,
    ) {
        preferenceRepository.setDefaultPreferencesIfNeeded(g2Color, g3Color, g4Color)
    }
}