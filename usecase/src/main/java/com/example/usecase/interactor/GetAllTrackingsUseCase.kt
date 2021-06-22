package com.example.usecase.interactor

import com.example.common.entity.Tracking
import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class GetAllTrackingsUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    suspend operator fun invoke(): List<Tracking> {
        return trackingRepository.getAllTrackings()
    }
}