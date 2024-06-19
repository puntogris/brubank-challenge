package com.puntogris.brubankchallenge.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LocalMovieDto(
    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo("title")
    val title: String,

    @ColumnInfo("poster_url")
    val posterUrl: String,

    @ColumnInfo("backdrop_url")
    val backdropUrl: String,

    @ColumnInfo("overview")
    val overview: String,

    @ColumnInfo("release_date")
    val releaseDate: String,

    @ColumnInfo("primary_genre")
    val primaryGenre: String
)