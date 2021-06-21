package com.example.usecase.interactor

import com.example.common.entity.Tracking
import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class GetActiveTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(): Tracking {
        return trackingRepository.getActiveTracking()
    }
}