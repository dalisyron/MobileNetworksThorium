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

    private val _appState: Channel<AppState> = Channel()
    override val appState: Flow<AppState> = _appState.receiveAsFlow()

    override suspend fun goToState(state: AppState) {
        if (state == currentState) return

        val oldState = currentState
        currentState = state
        _appState.send(state)

        when (oldState) {
            AppState.Dashboard -> {
                when (state) {
                    AppState.Home -> {
                        _navigationAction.send(NavigationAction.FromDashboardToHome)
                    }
                    AppState.Settings -> {
                        _navigationAction.send(NavigationAction.FromDashboardToSettings)
                    }
                }
            }
            AppState.Home -> {
                when (state) {
                    AppState.Dashboard -> {
                        _navigationAction.send(NavigationAction.FromHomeToDashboard)
                    }
                    AppState.Settings -> {
                        _navigationAction.send(NavigationAction.FromHomeToSettings)
                    }
                }
            }
            AppState.Settings -> {
                when (state) {
                    AppState.Dashboard -> {
                        _navigationAction.send(NavigationAction.FromSettingsToDashboard)
                    }
                    AppState.Home -> {
                        _navigationAction.send(NavigationAction.FromSettingsToHome)
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
                    AppState.Settings -> {
                        _navigationAction.send(NavigationAction.StartSettings)
                    }
                }
            }
        }
    }
}