package com.example.androidfundamental.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int ,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "media_cover")
    val mediaCover: String?,

    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean
)
