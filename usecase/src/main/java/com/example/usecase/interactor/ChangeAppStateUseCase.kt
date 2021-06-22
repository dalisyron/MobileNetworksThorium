package com.example.usecase.interactor

import com.example.common.entity.AppState
import com.example.usecase.repository.AppStateRepository
import javax.inject.Inject

class ChangeAppStateUseCase @Inject constructor(
    private val appStateRepository: AppStateRepository
) {

    suspend operator fun invoke(newState: AppState) {
        appStateRepository.goToState(newState)
    }
}