package com.example.usecase.interactor

import com.example.usecase.repository.TrackingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsThereActiveTrackingUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return trackingRepository.isThereActiveTrackingFlow()
    }
}