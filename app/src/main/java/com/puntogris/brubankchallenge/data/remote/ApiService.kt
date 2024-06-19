package com.puntogris.brubankchallenge.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search/movie")
    suspend fun searchMovie(@Query("query") query: String): RemoteMovieListDto

    @GET("genre/movie/list")
    suspend fun getMoviesGenres(): RemoteGenreListDto

    @GET("discover/movie")
    suspend fun getRecommendedMovies(
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): RemoteMovieListDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val BASE_POSTER_IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val BASE_BACKDROP_IMG_URL = "https://image.tmdb.org/t/p/w780"
        const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YzVlODIzNGJkMzFkYTU5NDUwYzcyOGI4ZTY0ZWNhMyIsInN1YiI6IjYxY2E3NDU1YjU0MDAyMDA5OTQ4YjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XRJ51pOzDZQcNrLB2JpGJ_yHXz2cxJSvoEpbM4VLPjA"
    }
}