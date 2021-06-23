package com.example.thorium.ui.home

import androidx.lifecycle.*
import com.example.common.entity.CellLogRequest
import com.example.common.entity.Tracking
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startNewTrackingUseCase: StartNewTrackingUseCase,
    private val saveCellLogUseCase: SaveCellLogUseCase,
    private val stopActiveTrackingUseCase: StopActiveTrackingUseCase,
    private val isThereActiveTrackingUseCase: IsThereActiveTrackingUseCase,
    private val getSelectedForDisplayTracking: GetSelectedForDisplayTracking
) : ViewModel() {

    private val _alert: SingleLiveEvent<String> = SingleLiveEvent()
    val alert: LiveData<String> = _alert

    private val _displayedTracking: MutableLiveData<Tracking?> = MutableLiveData()
    val displayedTracking: LiveData<Tracking?> = _displayedTracking

    val isThereActiveTracking: LiveData<Boolean> = isThereActiveTrackingUseCase().asLiveData()

    fun initialize() {
        viewModelScope.launch {
            updateDisplayedTracking()
        }
    }

    private fun runUseCase(successMessage: String, useCase:suspend () -> Unit) {
        viewModelScope.launch {
            try {
                useCase()
                _alert.value = successMessage
            } catch (e: Exception) {
                _alert.value = e.message
            }
        }
    }

    private fun onStartTrackingClicked() {
        runUseCase(successMessage = "Successfully started new tracking") {
            startNewTrackingUseCase()
            updateDisplayedTracking()
        }
    }

    private fun onStopTrackingClicked() {
        runUseCase(successMessage = "Successfully stopped active tracking") {
            stopActiveTrackingUseCase()
            updateDisplayedTracking()
        }
    }

    fun onSaveCellLogClicked(cellLogRequest: CellLogRequest) {
        runUseCase(successMessage = "Successfully saved cell log") {
            saveCellLogUseCase(cellLogRequest)
            updateDisplayedTracking()
        }
    }

    private suspend fun updateDisplayedTracking() {
        _displayedTracking.value = getSelectedForDisplayTracking()
    }

    fun onStartStopTrackingClicked() {
        if (isThereActiveTracking.value!!) {
            onStopTrackingClicked()
        } else {
            onStartTrackingClicked()
        }
    }
}