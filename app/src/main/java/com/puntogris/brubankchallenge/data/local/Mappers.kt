package com.puntogris.brubankchallenge.data.local

import com.puntogris.brubankchallenge.data.local.models.LocalMovieDto
import com.puntogris.brubankchallenge.domain.models.Movie

fun LocalMovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        overview = overview,
        releaseDate = releaseDate,
        primaryGenre = primaryGenre,
        isFavorite = true
    )
}

fun List<LocalMovieDto>.toMovies(): List<Movie> {
    return map { it.toMovie() }
}

fun Movie.toLocalMovieDto(): LocalMovieDto {
    return LocalMovieDto(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterUrl,
        releaseDate = releaseDate,
        primaryGenre = primaryGenre,
        backdropUrl = backdropUrl
    )
}