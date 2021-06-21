package com.example.usecase.interactor

import com.example.common.entity.Tracking
import com.example.usecase.repository.TrackingRepository

class GetActiveTrackingUseCase constructor(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(): Tracking {
        return trackingRepository.getActiveTracking()
    }
}