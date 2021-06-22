package com.example.usecase.interactor

import com.example.common.entity.Tracking
import com.example.usecase.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSelectedForDisplayTracking @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(): Tracking? {
        val isThereActiveTracking = trackingRepository.isThereActiveTracking()
        return if (isThereActiveTracking) {
            trackingRepository.getActiveTracking()
        } else {
            trackingRepository.selectedTrackingId?.let {
                trackingRepository.getTrackingById(it)
            }
        }
    }
}