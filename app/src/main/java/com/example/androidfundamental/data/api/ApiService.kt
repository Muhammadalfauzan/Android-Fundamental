package com.example.androidfundamental.data.api

import com.example.androidfundamental.data.apimodel.ResponseEventDetail
import com.example.androidfundamental.data.apimodel.ResponseUpcoming
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/events")
    suspend fun getEvents(
        @Query("active") active: Int = 1,
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 40
    ): Response <ResponseUpcoming>

    @GET("/events/{id}")
    suspend fun getEventDetail(@Path("id") id: Int): Response<ResponseEventDetail>
}