package com.puntogris.brubankchallenge.domain

import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IRepository {

    suspend fun searchMovies(query: String): Resource<List<Movie>>

    suspend fun addMovieToFavorites(movie: Movie)

    suspend fun removeMovieFromFavorites(movie: Movie)

    suspend fun getRecommendedMovies(page: Int): Flow<Resource<List<Movie>>>

    fun getFavoriteMovies(): Flow<List<Movie>>
}