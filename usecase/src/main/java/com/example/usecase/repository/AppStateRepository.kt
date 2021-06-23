package com.example.usecase.repository

import com.example.common.entity.AppState
import com.example.common.entity.NavigationAction
import kotlinx.coroutines.flow.Flow

interface AppStateRepository {

    val navigationAction: Flow<NavigationAction>
    val appState: Flow<AppState>

    suspend fun goToState(state: AppState)
}