package com.example.androidfundamental

import com.example.androidfundamental.data.api.ApiConfig
import com.example.androidfundamental.data.apimodel.EventRepository

object Injection {

    fun provideEventRepository(): EventRepository {
        val apiService = ApiConfig.getApiService() // Mendapatkan instance ApiService
        return EventRepository(apiService) // Mengembalikan instance EventRepository dengan ApiService
    }
}
