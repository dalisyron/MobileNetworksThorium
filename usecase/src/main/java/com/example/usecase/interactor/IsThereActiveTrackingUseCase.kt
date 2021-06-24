package com.example.usecase.interactor

import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class IsThereActiveTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {
    suspend operator fun invoke(): Boolean {
        return trackingRepository.isThereActiveTracking()
    }
}