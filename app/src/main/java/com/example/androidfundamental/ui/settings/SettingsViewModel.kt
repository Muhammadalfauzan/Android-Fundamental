package com.example.androidfundamental.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidfundamental.utils.SettingPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private  val pref : SettingPreferences) : ViewModel() {
    fun getThemeSettings() : LiveData<Boolean> {
        return  pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive : Boolean){
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }
}