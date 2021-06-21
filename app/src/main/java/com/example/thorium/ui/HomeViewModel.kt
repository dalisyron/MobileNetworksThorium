package com.example.thorium.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.Tracking
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.GetActiveTrackingUseCase
import com.example.usecase.interactor.RecordCellLogUseCase
import com.example.usecase.interactor.StartNewTrackingUseCase
import com.example.usecase.interactor.StopActiveTrackingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val startNewTrackingUseCase: StartNewTrackingUseCase,
    private val recordCellLogUseCase: RecordCellLogUseCase,
    private val stopActiveTrackingUseCase: StopActiveTrackingUseCase,
    private val getActiveTrackingUseCase: GetActiveTrackingUseCase
) : ViewModel() {

    private val _alert: SingleLiveEvent<String> = SingleLiveEvent()
    val alert: LiveData<String> = _alert

    private val _activeTracking: MutableLiveData<Tracking> = MutableLiveData()
    val activeTracking = _activeTracking

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

    fun onStartTrackingClicked() {
        runUseCase(successMessage = "Successfully started new tracking") {
            startNewTrackingUseCase()
        }
    }

    fun onStopTrackingClicked() {
        runUseCase(successMessage = "Successfully stopped active tracking") {
            stopActiveTrackingUseCase()
        }
    }

    fun onSaveCellLogClicked() {
        runUseCase(successMessage = "Successfully saved cell log") {
            recordCellLogUseCase()
            _activeTracking.value = getActiveTrackingUseCase()
        }
    }
}