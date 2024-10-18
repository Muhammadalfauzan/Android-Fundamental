package com.example.androidfundamental.data.apimodel

import com.example.androidfundamental.data.api.ApiService
import retrofit2.Response

class EventRepository(private val apiService: ApiService) {
    suspend fun getEvents(active: Int = 1, query: String? = null, limit: Int = 40): Response<ResponseUpcoming> {
        return apiService.getEvents(active, query, limit)
    }
}

