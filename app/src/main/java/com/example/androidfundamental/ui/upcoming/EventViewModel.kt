package com.example.androidfundamental.ui.upcoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfundamental.data.apimodel.EventRepository
import com.example.androidfundamental.data.apimodel.ListEventsItem
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    // Exposing LiveData instead of MutableLiveData to avoid external modification
    private val _events = MutableLiveData<List<ListEventsItem?>?>()
    val events: LiveData<List<ListEventsItem?>?> = _events

    // Exposing error message LiveData
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Function to fetch events from repository
    fun fetchEvents(active: Int = 1, query: String? = null, limit: Int = 40) {
        viewModelScope.launch {
            Log.d("EventViewModel", "Fetching events from API...")
            try {
                val response = repository.getEvents(active, query, limit)
                Log.d("EventViewModel", "Response received, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.listEvents != null) {
                        Log.d("EventViewModel", "Events fetched: ${body.listEvents.size}")
                        _events.postValue(body.listEvents)
                    } else {
                        _errorMessage.postValue("No events available")
                        Log.d("EventViewModel", "No events available")
                    }
                } else {
                    _errorMessage.postValue("Failed to load events: ${response.message()}")
                    Log.d("EventViewModel", "API call failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
                Log.e("EventViewModel", "Error while fetching events: ${e.message}", e)
            }
        }
    }
}



