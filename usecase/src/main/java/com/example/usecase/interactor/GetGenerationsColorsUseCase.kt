package com.example.usecase.interactor

import com.example.common.entity.GenerationsColorsData
import com.example.usecase.repository.PreferenceRepository

class GetGenerationsColorsUseCase constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(): GenerationsColorsData {
        return preferenceRepository.getGenerationsColors()
    }
}