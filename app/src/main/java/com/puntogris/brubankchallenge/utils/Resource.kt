package com.puntogris.brubankchallenge.utils

import com.puntogris.brubankchallenge.R

sealed class Resource<T> {
    data class Success<T>(val value: T) : Resource<T>()
    data class Error<T>(val error: Int = R.string.copy_general_error) : Resource<T>()
    class Loading<T> : Resource<T>()
}