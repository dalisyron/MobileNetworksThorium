package com.example.thorium.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.entity.Preference
import com.example.thorium.datasource.DataStoreManager
import com.example.thorium.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SettingsViewModel @Inject constructor(
    dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _preferenceList = SingleLiveEvent<List<Preference>>()
    val preferenceList = _preferenceList

    init {
        viewModelScope.launch {
            val prefs = dataStoreManager.getAllPreferences()
            withContext(Dispatchers.Main) {
                preferenceList.value = prefs
            }
        }
    }
}