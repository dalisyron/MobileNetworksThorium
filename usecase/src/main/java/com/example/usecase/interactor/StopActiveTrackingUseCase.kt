package com.example.usecase.interactor

import com.example.common.entity.LatLng
import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class StopActiveTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(stoppingLocation: LatLng) {
        trackingRepository.stopActiveTracking(stoppingLocation)
    }
}