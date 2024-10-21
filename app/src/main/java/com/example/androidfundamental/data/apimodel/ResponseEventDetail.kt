package com.example.androidfundamental.data.apimodel

import com.google.gson.annotations.SerializedName

data class ResponseEventDetail(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("event")
    val event: ListEventsItem? = null // Use the existing ListEventsItem class
)