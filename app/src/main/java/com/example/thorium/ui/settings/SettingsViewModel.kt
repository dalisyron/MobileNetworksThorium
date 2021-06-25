package com.example.thorium.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.Preference
import com.example.thorium.util.SingleLiveEvent
import com.example.usecase.interactor.GetAllPreferencesUseCase
import com.example.usecase.interactor.SetPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val setPreferenceUseCase: SetPreferenceUseCase,
    private val getAllPreferencesUseCase: GetAllPreferencesUseCase,
) : ViewModel() {

    private val _preferenceList = SingleLiveEvent<List<Preference>>()
    val preferenceList = _preferenceList

    init {
        viewModelScope.launch {
            val prefs = getAllPreferencesUseCase.invoke()
            withContext(Dispatchers.Main) {
                preferenceList.value = prefs
            }
        }
    }

    fun setPreference(newPreference: Preference) {
        viewModelScope.launch {
            setPreferenceUseCase.invoke(newPreference)
        }
    }
}