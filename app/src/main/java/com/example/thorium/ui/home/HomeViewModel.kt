package com.example.thorium.ui.home

import androidx.lifecycle.*
import com.example.common.entity.CellLogRequest
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startNewTrackingUseCase: StartNewTrackingUseCase,
    private val saveCellLogUseCase: SaveCellLogUseCase,
    private val stopActiveTrackingUseCase: StopActiveTrackingUseCase,
    private val isThereActiveTrackingUseCase: IsThereActiveTrackingUseCase,
    private val getSelectedForDisplayTracking: GetSelectedForDisplayTracking,
    private val loadTrackingOnMapUseCase: LoadTrackingOnMapUseCase
) : ViewModel() {

    private val _alert: SingleLiveEvent<String> = SingleLiveEvent()
    val alert: LiveData<String> = _alert

    private val _displayedTracking: MutableLiveData<Tracking?> = MutableLiveData()
    val displayedTracking: LiveData<Tracking?> = _displayedTracking

    private val _isThereActiveTracking: MutableLiveData<Boolean> = MutableLiveData()
    val isThereActiveTracking: LiveData<Boolean> = _isThereActiveTracking

    private val _requestCellLog: SingleLiveEvent<LatLngEntity> = SingleLiveEvent()
    val requestCellLog: LiveData<LatLngEntity> = _requestCellLog

    private val _cellLogFinish: SingleLiveEvent<Unit> = SingleLiveEvent()
    val cellLogFinish = _cellLogFinish

    private val _showTrackingOnMap: MutableLiveData<Tracking> = MutableLiveData()
    val showTrackingOnMap: LiveData<Tracking> = _showTrackingOnMap

    fun initialize() {
        viewModelScope.launch {
            val tracking = getSelectedForDisplayTracking()
            if (loadTrackingOnMapUseCase()) {
                showMarkers(tracking)
            }
            _displayedTracking.value = tracking
        }
    }

    private fun showMarkers(tracking: Tracking?) {
        _showTrackingOnMap.value = tracking
    }

    private suspend fun runUseCase(successMessage: String, useCase: suspend () -> Unit) {
        try {
            useCase()
            _alert.value = successMessage
        } catch (e: Exception) {
            _alert.value = e.message
        }
    }

    private suspend fun onStartTrackingClicked(trackingAdd: TrackingAdd) {
        runUseCase(successMessage = "Successfully started new tracking") {
            startNewTrackingUseCase(trackingAdd)
            updateDisplayedTracking()
        }
    }

    private suspend fun onStopTrackingClicked(stoppingLocation: LatLngEntity) {
        runUseCase(successMessage = "Successfully stopped active tracking") {
            stopActiveTrackingUseCase(stoppingLocation)
            updateDisplayedTracking()
        }
    }

    fun onSaveCellLogClicked(cellLogRequest: CellLogRequest) {
        viewModelScope.launch {
            saveCellLogUseCase(cellLogRequest)
            updateDisplayedTracking()
        }
    }

    private suspend fun updateDisplayedTracking() {
        _displayedTracking.value = getSelectedForDisplayTracking()
    }

    private suspend fun updateActiveTrackingStatus() {
        _isThereActiveTracking.value = isThereActiveTrackingUseCase()
    }

    fun onStartStopTrackingClicked(currentLocation: LatLngEntity) {
        viewModelScope.launch {
            val isActive = isThereActiveTrackingUseCase()
            if (isActive) {
                onStopTrackingClicked(stoppingLocation = currentLocation)
            } else {
                onStartTrackingClicked(
                    TrackingAdd(
                        startLocation = currentLocation,
                        dateCreated = System.currentTimeMillis()
                    )
                )
            }
            updateActiveTrackingStatus()
        }
    }

    fun sendCellLog(cellLogRequest: CellLogRequest) {
        viewModelScope.launch {
            saveCellLogUseCase(cellLogRequest)
            _cellLogFinish.call()
            updateDisplayedTracking()
        }
    }

    // Go back and forth between activity and viewmodel since cell-log retrieval is computationally intensive
    fun onLocationUpdate(lastLocation: LatLngEntity?) {
        viewModelScope.launch {
            if (lastLocation != null) {
                val isActive = isThereActiveTrackingUseCase()
                if (isActive) {
                    _requestCellLog.value = lastLocation
                }
            }
        }
    }

    fun onModeChange(mode: String) {

    }
}