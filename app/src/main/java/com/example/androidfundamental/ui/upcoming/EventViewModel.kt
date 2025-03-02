package com.example.androidfundamental.ui.upcoming

import androidx.lifecycle.*
import com.example.androidfundamental.data.local.entity.FavoriteEvent
import com.example.androidfundamental.data.Resource
import com.example.androidfundamental.data.EventRepository
import com.example.androidfundamental.data.remote.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EventViewModel(
    private val repository: EventRepository,
) : ViewModel() {

    val upcomingEvents: LiveData<Resource<List<ListEventsItem?>>> =
        repository.getEvents(active = 1, query = null, limit = 40)

    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvents()
    private val _isFavorite = MutableStateFlow(false)

    val isFavorite: StateFlow<Boolean> get() = _isFavorite

    private val finishedEvents: LiveData<Resource<List<ListEventsItem?>>> =
        repository.getEvents(active = 0, query = null, limit = 40)


    private val finishedQueryLiveData = MutableLiveData<String?>()
    private val searchedFinishedEvents: LiveData<Resource<List<ListEventsItem?>>> =
        finishedQueryLiveData.switchMap { query ->
            repository.getEvents(active = 0, query = query, limit = 40)
        }
    val finalFinishedEvents = MediatorLiveData<Resource<List<ListEventsItem?>>>()

    init {
        finalFinishedEvents.addSource(finishedEvents) { result ->
            if (finishedQueryLiveData.value.isNullOrEmpty()) {
                finalFinishedEvents.value = result
            }
        }
        finalFinishedEvents.addSource(searchedFinishedEvents) { result ->
            finalFinishedEvents.value = result
        }
    }

    fun searchFinishedEvents(query: String?) {
        finishedQueryLiveData.value = query
    }

    fun getEventDetail(id: Int): LiveData<Resource<ListEventsItem?>> {
        val result = MutableLiveData<Resource<ListEventsItem?>>()
        result.value = Resource.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getEventDetail(id).execute()
                if (response.isSuccessful) {
                    val eventDetail = response.body()?.event
                    result.postValue(Resource.Success(eventDetail))
                } else {
                    result.postValue(Resource.Error("Gagal mengambil data event"))
                }
            } catch (e: Exception) {
                result.postValue(Resource.Error("Terjadi kesalahan: ${e.message}"))
            }
        }

        return result
    }

    fun addToFavorite(event: FavoriteEvent) {
        repository.addToFavorite(event)
        _isFavorite.value = true
    }


    fun removeFromFavorite(eventId: Int) {
        repository.removeFromFavorite(eventId)
        _isFavorite.value = false
    }

    fun checkFavoriteStatus(eventId: Int) {
        repository.isFavorite(eventId) { isFav ->
            _isFavorite.value = isFav
        }
    }
}