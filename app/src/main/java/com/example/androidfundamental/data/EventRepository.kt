package com.example.androidfundamental.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.androidfundamental.utils.AppExecutors
import com.example.androidfundamental.data.local.entity.FavoriteEvent
import com.example.androidfundamental.data.remote.retrofit.ApiService
import com.example.androidfundamental.data.local.room.FavoriteEventDao
import com.example.androidfundamental.data.remote.response.ListEventsItem
import com.example.androidfundamental.data.remote.response.ResponseEventDetail
import com.example.androidfundamental.data.remote.response.ResponseUpcoming
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: FavoriteEventDao,
    private val appExecutors: AppExecutors,
) {

    fun getEvents(
        active: Int,
        query: String?,
        limit: Int,
    ): LiveData<Resource<List<ListEventsItem?>>> {
        val result = MediatorLiveData<Resource<List<ListEventsItem?>>>()
        result.value = Resource.Loading

        apiService.getEvents(active, query, limit).enqueue(object : Callback<ResponseUpcoming> {
            override fun onResponse(
                call: Call<ResponseUpcoming>,
                response: Response<ResponseUpcoming>,
            ) {
                if (response.isSuccessful) {
                    val events = response.body()?.listEvents ?: emptyList()
                    result.postValue(Resource.Success(events))
                } else {
                    result.postValue(Resource.Error("Gagal mendapatkan data: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<ResponseUpcoming>, t: Throwable) {
                result.postValue(Resource.Error("Koneksi gagal: ${t.message}"))
            }
        })

        return result
    }

    // Mengambil detail event dari API
    fun getEventDetail(id: Int): Call<ResponseEventDetail> {
        return apiService.getEventDetail(id)
    }


    fun isFavorite(eventId: Int, callback: (Boolean) -> Unit) {
        appExecutors.diskIO.execute {
            val isFav = eventDao.getFavoriteEventById(eventId) != null
            appExecutors.mainThread.execute { callback(isFav) }
        }
    }

    //  Menambahkan event ke favorit
    fun addToFavorite(event: FavoriteEvent) {
        appExecutors.diskIO.execute {
            eventDao.insertFavorite(event)
        }
    }

    // Menghapus event dari favorit berdasarkan ID
    fun removeFromFavorite(eventId: Int) {
        appExecutors.diskIO.execute {
            val event = eventDao.getFavoriteEventById(eventId)
            if (event != null) {
                eventDao.deleteFavorite(event)
            }
        }
    }

    // Mengambil semua event favorit
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = eventDao.getAllFavorites()

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: FavoriteEventDao,
            appExecutors: AppExecutors,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also { instance = it }
    }
}