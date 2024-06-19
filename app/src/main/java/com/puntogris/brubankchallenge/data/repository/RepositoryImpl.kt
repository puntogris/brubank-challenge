package com.puntogris.brubankchallenge.data.repository

import com.puntogris.brubankchallenge.data.local.GenresDao
import com.puntogris.brubankchallenge.data.local.models.LocalGenreDto
import com.puntogris.brubankchallenge.data.local.MoviesDao
import com.puntogris.brubankchallenge.data.local.toLocalMovieDto
import com.puntogris.brubankchallenge.data.local.toMovies
import com.puntogris.brubankchallenge.data.remote.ApiService
import com.puntogris.brubankchallenge.data.remote.RemoteMovieDto
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.DispatcherProvider
import com.puntogris.brubankchallenge.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val apiService: ApiService,
    private val moviesDao: MoviesDao,
    private val genresDao: GenresDao,
    private val dispatchers: DispatcherProvider
) : IRepository {

    override suspend fun searchMovies(query: String): Resource<List<Movie>> =
        withContext(dispatchers.io) {
            try {
                if (query.isEmpty()) {
                    return@withContext Resource.Success(emptyList())
                }
                val results = apiService.searchMovie(query).results
                val movies = results.map { movie ->
                    async {
                        Movie(
                            id = movie.id,
                            title = movie.title,
                            posterUrl = ApiService.BASE_POSTER_IMG_URL + movie.posterPath.orEmpty(),
                            backdropUrl = ApiService.BASE_BACKDROP_IMG_URL + movie.backdropPath.orEmpty(),
                            releaseDate = movie.releaseDate.orEmpty(),
                            overview = movie.overview.orEmpty(),
                            primaryGenre = getLocalGenreOrFetchRemote(movie),
                            isFavorite = moviesDao.getFavoriteMovie(movie.id) != null
                        )
                    }
                }.awaitAll()

                Resource.Success(movies)
            } catch (e: Exception) {
                Resource.Error()
            }
        }

    override suspend fun addMovieToFavorites(movie: Movie) = withContext(dispatchers.io) {
        moviesDao.insert(movie.toLocalMovieDto())
    }

    override suspend fun removeMovieFromFavorites(movie: Movie) = withContext(dispatchers.io) {
        moviesDao.delete(movie.id)
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        return moviesDao.getFavoriteMovies()
            .map { movies -> movies.toMovies() }
            .flowOn(dispatchers.io)
    }

    override suspend fun getRecommendedMovies(page: Int): Flow<Resource<List<Movie>>> = flow {
        try {
            emit(Resource.Loading())

            val results = apiService.getRecommendedMovies(page = page).results
            val movies = coroutineScope {
                results.map { movie ->
                    async {
                        Movie(
                            id = movie.id,
                            title = movie.title,
                            posterUrl = ApiService.BASE_POSTER_IMG_URL + movie.posterPath.orEmpty(),
                            backdropUrl = ApiService.BASE_BACKDROP_IMG_URL + movie.backdropPath.orEmpty(),
                            overview = movie.overview.orEmpty(),
                            releaseDate = movie.releaseDate.orEmpty(),
                            primaryGenre = getLocalGenreOrFetchRemote(movie),
                            isFavorite = moviesDao.getFavoriteMovie(movie.id) != null
                        )
                    }
                }.awaitAll()
            }

            emit(Resource.Success(movies))
        } catch (e: Exception) {
            emit(Resource.Error())
        }
    }.flowOn(dispatchers.io)


    private suspend fun getLocalGenreOrFetchRemote(movie: RemoteMovieDto): String {
        var primaryGenre = ""

        try {
            val localGenre = genresDao.getGenre(movie.genreIds.firstOrNull() ?: 0)
            if (localGenre == null) {
                val remoteGenres = apiService.getMoviesGenres().genres.map {
                    LocalGenreDto(it.id, it.name)
                }
                if (remoteGenres.isNotEmpty()) {
                    primaryGenre = remoteGenres.first().name
                }
                genresDao.insert(remoteGenres)
            } else {
                primaryGenre = localGenre.name
            }
        } catch (ignored: Exception) {
            // TODO: handle exception
        }

        return primaryGenre
    }
}