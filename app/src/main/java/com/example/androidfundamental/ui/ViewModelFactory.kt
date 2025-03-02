package com.example.androidfundamental.ui

import com.example.androidfundamental.ui.upcoming.EventViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidfundamental.data.EventRepository
import com.example.androidfundamental.utils.SettingPreferences
import com.example.androidfundamental.ui.settings.SettingsViewModel

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences? = null
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                settingPreferences?.let {
                    return SettingsViewModel(it) as T
                } ?: throw IllegalArgumentException("Preferences required for SettingsViewModel")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(
            repository: EventRepository,
            preferences: SettingPreferences? = null
        ): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(repository, preferences)
            }.also { instance = it }
    }
}





