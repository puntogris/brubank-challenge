package com.puntogris.brubankchallenge.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteMovieListDto(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<RemoteMovieDto>
)

data class RemoteMovieDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("overview")
    val overview: String?,

    @SerializedName("release_date")
    val releaseDate: String?,

    @SerializedName("genre_ids")
    val genreIds: List<Int>
)
