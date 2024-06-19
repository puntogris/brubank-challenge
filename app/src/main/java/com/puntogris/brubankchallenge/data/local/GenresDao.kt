package com.puntogris.brubankchallenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puntogris.brubankchallenge.data.local.models.LocalGenreDto

@Dao
interface GenresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(genres: List<LocalGenreDto>)

    @Query("SELECT * FROM LocalGenreDto where id = :id LIMIT 1")
    suspend fun getGenre(id: Int): LocalGenreDto?
}