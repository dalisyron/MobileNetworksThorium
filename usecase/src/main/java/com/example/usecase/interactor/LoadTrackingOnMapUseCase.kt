package com.example.usecase.interactor

import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class LoadTrackingOnMapUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    operator fun invoke(): Boolean {
        return trackingRepository.selectedTrackingId != null
    }
}