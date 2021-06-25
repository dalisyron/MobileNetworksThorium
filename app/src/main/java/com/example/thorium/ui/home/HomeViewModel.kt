package com.example.thorium.ui.home

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.CellLogRequest
import com.example.common.entity.GenerationsColorsData
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import com.example.thorium.R
import com.example.thorium.app.ThoriumApp
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.GetGenerationsColorsUseCase
import com.example.usecase.interactor.GetSelectedForDisplayTracking
import com.example.usecase.interactor.IsThereActiveTrackingUseCase
import com.example.usecase.interactor.LoadTrackingOnMapUseCase
import com.example.usecase.interactor.SaveCellLogUseCase
import com.example.usecase.interactor.StartNewTrackingUseCase
import com.example.usecase.interactor.StopActiveTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startNewTrackingUseCase: StartNewTrackingUseCase,
    private val saveCellLogUseCase: SaveCellLogUseCase,
    private val stopActiveTrackingUseCase: StopActiveTrackingUseCase,
    private val isThereActiveTrackingUseCase: IsThereActiveTrackingUseCase,
    private val getSelectedForDisplayTracking: GetSelectedForDisplayTracking,
    private val loadTrackingOnMapUseCase: LoadTrackingOnMapUseCase,
    private val getGenerationsColorsUseCase: GetGenerationsColorsUseCase
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

    private val _trackingMode: SingleLiveEvent<TrackingMode> = SingleLiveEvent()
    val trackingMode = _trackingMode

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
        when (mode) {
            extractString(R.string.mode_generation) -> {
                viewModelScope.launch {
                    val generationsColors = getGenerationsColorsUseCase()
                    _trackingMode.value = TrackingMode.Generation(
                        generationsColors
                    )
                }
            }
            extractString(R.string.mode_location) -> TrackingMode.Location
            extractString(R.string.mode_strength) -> TrackingMode.Strength
            extractString(R.string.mode_code) -> TrackingMode.Code
            else -> TrackingMode.Location
        }
    }

    private fun extractString(@StringRes resId: Int): String {
        return ThoriumApp.applicationContext?.resources?.getString(resId)!!
    }
}

sealed class TrackingMode {
    data class Generation(
        val generationsColorsData: GenerationsColorsData
    ) : TrackingMode()

    object Code : TrackingMode()
    object Location : TrackingMode()
    object Strength : TrackingMode()
}