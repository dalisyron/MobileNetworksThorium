package com.example.data.repository

import com.example.common.entity.AppState
import com.example.common.entity.NavigationAction
import com.example.usecase.repository.AppStateRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class AppStateRepositoryImpl : AppStateRepository {

    private var currentState: AppState? = null

    private val _navigationAction: Channel<NavigationAction> = Channel()

    override val navigationAction: Flow<NavigationAction>
        get() = _navigationAction.receiveAsFlow()

    override suspend fun goToState(state: AppState) {
        if (state == currentState) return

        val oldState = currentState
        currentState = state

        when (oldState) {
            AppState.Dashboard -> {
                when (state) {
                    AppState.Home -> {
                        _navigationAction.send(NavigationAction.FromDashboardToHome)
                    }
                }
            }
            AppState.Home -> {
                when (state) {
                    AppState.Dashboard -> {
                        _navigationAction.send(NavigationAction.FromHomeToDashboard)
                    }
                }
            }
            null -> {
                when (state) {
                    AppState.Home -> {
                        _navigationAction.send(NavigationAction.StartHome)
                    }
                    AppState.Dashboard -> {
                        _navigationAction.send(NavigationAction.StartDashboard)
                    }
                }
            }
        }
    }
}