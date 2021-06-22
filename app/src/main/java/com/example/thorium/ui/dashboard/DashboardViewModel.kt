package com.example.thorium.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.Tracking
import com.example.usecase.interactor.GetAllTrackingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllTrackingsUseCase: GetAllTrackingsUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            val trackings = getAllTrackingsUseCase()
            _trackings.value = trackings
        }
    }

    private val _trackings: MutableLiveData<List<Tracking>> = MutableLiveData()
    val trackings: LiveData<List<Tracking>> = _trackings
}