package com.example.androidfundamental.di

import com.example.androidfundamental.utils.AppExecutors
import com.example.androidfundamental.data.remote.retrofit.ApiConfig
import com.example.androidfundamental.data.EventRepository
import com.example.androidfundamental.data.local.room.FavoriteDatabase
import android.content.Context

object Injection {
    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteDatabase.getInstance(context)
        val dao = database.newsDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, dao, appExecutors)

    }
}