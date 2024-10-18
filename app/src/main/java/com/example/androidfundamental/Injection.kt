package com.example.androidfundamental

import android.content.Context
import com.example.androidfundamental.data.api.ApiConfig
import com.example.androidfundamental.data.apimodel.EventRepository

object Injection {

    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService() // Mendapatkan instance ApiService
        return EventRepository(apiService) // Mengembalikan instance EventRepository dengan ApiService
    }
}
