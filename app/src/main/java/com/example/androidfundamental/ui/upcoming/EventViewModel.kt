package com.example.androidfundamental.ui.upcoming


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidfundamental.NetworkResult
import com.example.androidfundamental.data.apimodel.EventRepository
import com.example.androidfundamental.data.apimodel.ListEventsItem
import com.example.androidfundamental.data.apimodel.ResponseUpcoming
import kotlinx.coroutines.launch
import retrofit2.Response
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.distinctUntilChanged
import com.example.androidfundamental.data.apimodel.ResponseEventDetail

class EventViewModel(
    private val repository: EventRepository,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {

    // LiveData untuk upcoming events
    private val _upcomingEvents = MutableLiveData<NetworkResult<List<ListEventsItem?>>>()
    val upcomingEvents: LiveData<NetworkResult<List<ListEventsItem?>>> = _upcomingEvents.distinctUntilChanged()

    // LiveData untuk finished events
    private val _finishedEvents = MutableLiveData<NetworkResult<List<ListEventsItem?>>>()
    val finishedEvents: LiveData<NetworkResult<List<ListEventsItem?>>> = _finishedEvents.distinctUntilChanged()

    // LiveData untuk event detail
    private val _eventDetail = MutableLiveData<NetworkResult<ListEventsItem>>()
    val eventDetail: LiveData<NetworkResult<ListEventsItem>> = _eventDetail

    // Query terakhir yang digunakan
    var lastQuery: String?
        get() = savedStateHandle.get("lastQuery")
        set(value) = savedStateHandle.set("lastQuery", value)

    // Menyimpan apakah data sudah di-fetch
    var hasFetchedUpcomingEvents: Boolean
        get() = savedStateHandle.get("hasFetchedUpcomingEvents") ?: false
        set(value) = savedStateHandle.set("hasFetchedUpcomingEvents", value)

    var hasFetchedFinishedEvents: Boolean
        get() = savedStateHandle.get("hasFetchedFinishedEvents") ?: false
        set(value) = savedStateHandle.set("hasFetchedFinishedEvents", value)

    // Fetch upcoming events
    fun fetchUpcomingEvents(query: String? = null, limit: Int = 40) {
        if (query != lastQuery || !hasFetchedUpcomingEvents) {
            viewModelScope.launch {
                _upcomingEvents.postValue(NetworkResult.Loading())

                if (hasInternetConnection()) {
                    try {
                        val response = repository.getEvents(active = 1, query = query, limit = limit)
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.listEvents != null) {
                                _upcomingEvents.postValue(NetworkResult.Success(body.listEvents))
                                hasFetchedUpcomingEvents = true
                                lastQuery = query // Simpan query yang terakhir digunakan
                                savedStateHandle.set("upcomingEvents", body.listEvents) // Simpan data
                            } else {
                                _upcomingEvents.postValue(NetworkResult.Error("No upcoming events found"))
                            }
                        } else {
                            _upcomingEvents.postValue(NetworkResult.Error("Failed to load upcoming events"))
                        }
                    } catch (e: Exception) {
                        _upcomingEvents.postValue(NetworkResult.Error("Error: ${e.message}"))
                    }
                } else {
                    _upcomingEvents.postValue(NetworkResult.Error("No internet connection"))
                }
            }
        } else {
            _upcomingEvents.postValue(NetworkResult.Success(savedStateHandle.get("upcomingEvents") ?: emptyList()))
        }
    }

    // Fetch finished events (untuk mendukung pencarian query)
    fun fetchFinishedEvents(query: String? = null, limit: Int = 40) {
        if (query != lastQuery || !hasFetchedFinishedEvents) {
            viewModelScope.launch {
                _finishedEvents.postValue(NetworkResult.Loading())

                if (hasInternetConnection()) {
                    try {
                        val response = repository.getEvents(active = 0, query = query, limit = limit)
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.listEvents != null) {
                                _finishedEvents.postValue(NetworkResult.Success(body.listEvents))
                                hasFetchedFinishedEvents = true
                                lastQuery = query // Simpan query yang terakhir digunakan
                                savedStateHandle.set("finishedEvents", body.listEvents) // Simpan data
                            } else {
                                _finishedEvents.postValue(NetworkResult.Error("No finished events found"))
                            }
                        } else {
                            _finishedEvents.postValue(NetworkResult.Error("Failed to load finished events"))
                        }
                    } catch (e: Exception) {
                        _finishedEvents.postValue(NetworkResult.Error("Error: ${e.message}"))
                    }
                } else {
                    _finishedEvents.postValue(NetworkResult.Error("No internet connection"))
                }
            }
        } else {
            _finishedEvents.postValue(NetworkResult.Success(savedStateHandle.get("finishedEvents") ?: emptyList()))
        }
    }
    // Fetch event detail dari repository berdasarkan event ID
    fun fetchEventDetail(id: Int) {
        viewModelScope.launch {
            _eventDetail.postValue(NetworkResult.Loading()) // Set state to loading
            Log.d("EventViewModel", "Fetching event detail from API with id: $id")

            if (hasInternetConnection()) {
                try {
                    val response = repository.getEventDetail(id)
                    _eventDetail.postValue(handleEventDetailResponse(response))
                } catch (e: Exception) {
                    _eventDetail.postValue(NetworkResult.Error("Error: ${e.message}"))
                    Log.e("EventViewModel", "Error while fetching event detail: ${e.message}", e)
                }
            } else {
                _eventDetail.postValue(NetworkResult.Error("No internet connection"))
            }
        }
    }

    // Fungsi untuk menangani response detail event
    private fun handleEventDetailResponse(response: Response<ResponseEventDetail>): NetworkResult<ListEventsItem> {
        return when {
            response.message().toString().contains("timeout", ignoreCase = true) -> {
                NetworkResult.Error("Timeout occurred")
            }

            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited")
            }

            response.isSuccessful -> {
                response.body()?.event?.let { event ->
                    NetworkResult.Success(event)
                } ?: NetworkResult.Error("Event detail not found")
            }

            else -> {
                NetworkResult.Error("Error: ${response.message()}")
            }
        }
    }

    // Helper function untuk cek koneksi internet
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Helper untuk handle response API events list
    private fun handleEventResponse(response: Response<ResponseUpcoming>): NetworkResult<List<ListEventsItem?>> {
        return when {
            response.message().toString().contains("timeout", ignoreCase = true) -> {
                NetworkResult.Error("Timeout occurred")
            }

            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited")
            }

            response.isSuccessful -> {
                val events = response.body()?.listEvents?.filterNotNull()
                if (!events.isNullOrEmpty()) {
                    NetworkResult.Success(events)
                } else {
                    NetworkResult.Error("No events found")
                }
            }

            else -> {
                NetworkResult.Error("Error: ${response.message()}")
            }
        }
    }
}


