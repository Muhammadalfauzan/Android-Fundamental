package com.example.androidfundamental.data.apimodel

import android.util.Log
import com.example.androidfundamental.data.api.ApiService
import retrofit2.Response

class EventRepository(private val apiService: ApiService) {
    suspend fun getEvents(active: Int, query: String?, limit: Int): Response<ResponseUpcoming> {
        Log.d("Repository", "Fetching events with query: $query")  // Log query yang dikirim
        return apiService.getEvents(active, query, limit)
    }
    // Fetch event detail by id
    suspend fun getEventDetail(id: Int) = apiService.getEventDetail(id)

}

