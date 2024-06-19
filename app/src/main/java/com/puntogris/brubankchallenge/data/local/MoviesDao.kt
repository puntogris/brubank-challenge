package com.puntogris.brubankchallenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puntogris.brubankchallenge.data.local.models.LocalMovieDto
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: LocalMovieDto)

    @Query("DELETE FROM LocalMovieDto where id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM LocalMovieDto WHERE id = :id LIMIT 1")
    suspend fun getFavoriteMovie(id: Int): LocalMovieDto?

    @Query("SELECT * FROM LocalMovieDto")
    fun getFavoriteMovies(): Flow<List<LocalMovieDto>>
}