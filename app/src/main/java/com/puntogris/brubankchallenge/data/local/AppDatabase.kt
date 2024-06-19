package com.puntogris.brubankchallenge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puntogris.brubankchallenge.data.local.models.LocalGenreDto
import com.puntogris.brubankchallenge.data.local.models.LocalMovieDto

@Database(entities = [LocalMovieDto::class, LocalGenreDto::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val genresDao: GenresDao
}