package com.example.androidfundamental.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidfundamental.data.local.entity.FavoriteEvent

@Dao
interface FavoriteEventDao {


    // Ambil event favorit berdasarkan ID

    @Query("SELECT * FROM favorite_events WHERE id = :eventId")
     fun getFavoriteEventById(eventId: Int): FavoriteEvent?

    @Query("SELECT * FROM favorite_events WHERE id = :eventId")
     fun isFavorite(eventId: Int): FavoriteEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertFavorite(event: FavoriteEvent)

    @Delete
     fun deleteFavorite(event: FavoriteEvent)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>
}



