package com.puntogris.brubankchallenge.data.remote

import com.google.gson.annotations.SerializedName

data class RemoteGenreListDto(
    @SerializedName("genres")
    val genres: List<RemoteGenreDto>
)

data class RemoteGenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)