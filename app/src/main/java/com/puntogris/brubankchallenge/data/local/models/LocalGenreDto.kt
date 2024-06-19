package com.puntogris.brubankchallenge.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalGenreDto(

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo("name")
    val name: String
)