package com.example.thorium.ui.main

import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.entity.AppState
import com.example.thorium.R
import com.example.usecase.interactor.ChangeAppStateUseCase
import com.example.usecase.repository.AppStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val changeAppStateUseCase: ChangeAppStateUseCase,
    appStateRepository: AppStateRepository
) : ViewModel() {

    val navigationAction = appStateRepository.navigationAction.asLiveData()
    val appState = appStateRepository.appState.asLiveData()

    fun onBottomNavigationItemSelected(@IdRes itemId: Int) {
        viewModelScope.launch {
            when (itemId) {
                R.id.navigation_home -> {
                    changeAppStateUseCase(AppState.Home)
                }
                R.id.navigation_dashboard -> {
                    changeAppStateUseCase(AppState.Dashboard)
                }
                R.id.navigation_settings -> {
                    changeAppStateUseCase(AppState.Settings)
                }
            }
        }
    }

    fun onStart() {
        viewModelScope.launch {
            changeAppStateUseCase(AppState.Home)
        }
    }
}