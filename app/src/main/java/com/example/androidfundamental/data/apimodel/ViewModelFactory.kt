package com.example.androidfundamental.data.apimodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.example.androidfundamental.Injection

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(Injection.provideEventRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
