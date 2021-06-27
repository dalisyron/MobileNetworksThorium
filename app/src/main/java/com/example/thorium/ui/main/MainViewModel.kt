package com.example.thorium.ui.main

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.common.entity.AppState
import com.example.thorium.R
import com.example.usecase.interactor.ChangeAppStateUseCase
import com.example.usecase.repository.AppStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val changeAppStateUseCase: ChangeAppStateUseCase,
    appStateRepository: AppStateRepository
) : ViewModel() {

    val navigationAction = appStateRepository.navigationAction.asLiveData()
    val appState = appStateRepository.appState.asLiveData()

    private val _timer = MutableLiveData<Int>()
    val timer: LiveData<Int> = _timer

    fun onStart() {
        viewModelScope.launch {
            changeAppStateUseCase(AppState.Home)
        }
    }

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

    fun onTimerChanged(time: Int) {
        _timer.value = time
    }

}