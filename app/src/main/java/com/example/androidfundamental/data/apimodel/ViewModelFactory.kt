package com.example.androidfundamental.data.apimodel

import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.example.androidfundamental.Injection

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.androidfundamental.data.apimodel.EventRepository

class ViewModelFactory(
    private val application: Application,
    private val repository: EventRepository,
    owner: SavedStateRegistryOwner, // Tambahkan ini untuk mendukung SavedStateHandle
    defaultArgs: Bundle? = null // Argument default opsional
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(repository, handle, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
