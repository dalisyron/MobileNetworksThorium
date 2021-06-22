package com.example.usecase.interactor

import com.example.common.entity.AppState
import com.example.usecase.repository.AppStateRepository
import com.example.usecase.repository.TrackingRepository
import javax.inject.Inject

class SelectTrackingInDashboardUseCase @Inject constructor(
    private val trackingRepository: TrackingRepository,
    private val appStateRepository: AppStateRepository
) {

    suspend operator fun invoke(selectedTrackingId: Int) {
        trackingRepository.selectedTrackingId = selectedTrackingId
        appStateRepository.goToState(AppState.Home)
    }
}