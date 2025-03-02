package com.example.androidfundamental.data.remote.retrofit

import com.example.androidfundamental.data.remote.response.ResponseEventDetail
import com.example.androidfundamental.data.remote.response.ResponseUpcoming
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/events")
     fun getEvents(
        @Query("active") active: Int = 1,
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 40
    ): Call<ResponseUpcoming>

    @GET("/events/{id}")
    fun getEventDetail(@Path("id") id: Int): Call<ResponseEventDetail>
}