package com.example.thorium.ui.home

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.CellGsm
import com.example.common.entity.CellLog
import com.example.common.entity.CellLogRequest
import com.example.common.entity.CellLte
import com.example.common.entity.CellWcdma
import com.example.common.entity.GenerationsColorsData
import com.example.common.entity.LatLngEntity
import com.example.common.entity.Tracking
import com.example.common.entity.TrackingAdd
import com.example.thorium.R
import com.example.thorium.app.ThoriumApp
import com.example.thorium.util.ColorUtils
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.GetGenerationsColorsUseCase
import com.example.usecase.interactor.GetSelectedForDisplayTracking
import com.example.usecase.interactor.IsThereActiveTrackingUseCase
import com.example.usecase.interactor.LoadTrackingOnMapUseCase
import com.example.usecase.interactor.SaveCellLogUseCase
import com.example.usecase.interactor.StartNewTrackingUseCase
import com.example.usecase.interactor.StopActiveTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.abs
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

    private val _addMarker: MutableLiveData<Pair<CellLog, Int>> = MutableLiveData()
    val addMarker = _addMarker

    private val _clearMarkers: SingleLiveEvent<Unit> = SingleLiveEvent()
    val clearMarkers: LiveData<Unit> = _clearMarkers

    var trackingModeString: String? = null

    private val rnd = Random(1000000007L)

    private val colorMap: HashMap<Long, Int> = hashMapOf()

    fun initialize() {
        viewModelScope.launch {
            val tracking = getSelectedForDisplayTracking()
            if (tracking != null) {
                _displayedTracking.value = tracking
            }
        }
        trackingModeString?.let {
            onModeChange(it)
        }
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
            val isActive = isThereActiveTrackingUseCase()
            if (!isActive) {
                _alert.value = "Error: No active trackings."
                return@launch
            }
            val cellLog = saveCellLogUseCase(cellLogRequest)
            updateDisplayedTracking()
            addColorForCellLog(cellLog)
            addMarkerForCellLog(cellLog)
        }
    }

    private fun addMarkersForTracking(tracking: Tracking) {
        tracking.cellLogs.forEach {
            addMarkerForCellLog(it)
        }
    }

    private fun addMarkerForCellLog(cellLog: CellLog) {
        when (trackingModeString) {
            extractString(R.string.mode_location) -> {
                val key = cellLog.cell.locationId
                if (!colorMap.containsKey(key)) {
                    addToColorMap(key)
                }
                _addMarker.value = Pair(cellLog, colorMap[cellLog.cell.locationId]!!)
            }
            extractString(R.string.mode_code) -> {
                val key = cellLog.cell.cellCode
                if (!colorMap.containsKey(key)) {
                    addToColorMap(key)
                }
                _addMarker.value = Pair(cellLog, colorMap[cellLog.cell.cellCode]!!)
            }
            extractString(R.string.mode_generation) -> {
                viewModelScope.launch {
                    val colorData = getGenerationsColorsUseCase()
                    val color = when(cellLog.cell) {
                        is CellLte -> colorData.g4Color
                        is CellWcdma -> colorData.g3Color
                        is CellGsm -> colorData.g2Color
                        else -> throw IllegalArgumentException()
                    }
                    _addMarker.value = Pair(cellLog, ColorUtils.getColor(ColorUtils.mapFromIntToRes(color)))
                }
            }
        }
    }

    private fun addColorForCellLog(cellLog: CellLog) {
        when (trackingModeString) {
            extractString(R.string.mode_location) -> {
                val locationId = cellLog.cell.locationId
                addToColorMap(locationId)
            }
            extractString(R.string.mode_code) -> {
                val cellCode = cellLog.cell.cellCode
                addToColorMap(cellCode)
            }
            else -> {
            }
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
        trackingModeString = mode
        clearColorMap()
        _clearMarkers.call()

        viewModelScope.launch {
            getSelectedForDisplayTracking()?.let {
                createColorMap(it)
                addMarkersForTracking(it)
            }
        }
    }

    private fun createColorMap(tracking: Tracking) {
        tracking.cellLogs.forEach {
            addColorForCellLog(it)
        }
    }

    private fun addToColorMap(key: Long) {
        if (colorMap.containsKey(key)) return

        for (i in 1..1000) {
            val (r, g, b) = Triple(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            val isNewColor = colorMap.keys.none {
                val itRed: Int = ColorUtils.red(it).toInt()
                val itGreen: Int = ColorUtils.green(it).toInt()
                val itBlue: Int = ColorUtils.blue(it).toInt()
                val diff = abs(itRed - r) + abs(itGreen - g) + abs(itBlue - b)
                diff <= 30
            }
            if (isNewColor || i == 999) {
                colorMap[key] = Color.rgb(r, g, b)
                break
            }
        }
    }

    private fun extractString(@StringRes resId: Int): String {
        return ThoriumApp.applicationContext?.resources?.getString(resId)!!
    }

    private fun clearColorMap() {
        colorMap.clear()
    }
}