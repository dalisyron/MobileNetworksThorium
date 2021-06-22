package com.example.usecase.interactor

import com.example.common.entity.Tracking
import com.example.usecase.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSelectedForDisplayTracking @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    operator fun invoke(): Flow<Tracking> {
        return trackingRepository.isThereActiveTracking()
            .map { isThereActive ->
                return@map if (isThereActive) {
                    trackingRepository.getActiveTracking()
                } else {
                    trackingRepository.getTrackingById(trackingRepository.selectedTrackingId!!)
                }
            }
    }
}