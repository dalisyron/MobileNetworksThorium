package com.example.usecase.interactor

import com.example.common.entity.GenerationsColorsData
import com.example.usecase.repository.PreferenceRepository
import javax.inject.Inject

class GetGenerationsColorsUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend operator fun invoke(): GenerationsColorsData {
        return preferenceRepository.getGenerationsColors()
    }
}