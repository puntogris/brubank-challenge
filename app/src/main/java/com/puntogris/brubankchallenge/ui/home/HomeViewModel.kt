package com.puntogris.brubankchallenge.ui.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: IRepository
) : ViewModel() {

    val favoriteMovies = repository.getFavoriteMovies()

    val recommendedMovies = mutableStateListOf<Movie>()

    private val _errorChannel = Channel<Int>()
    val errorChannel = _errorChannel.receiveAsFlow()

    private val currentMoviesPage = MutableStateFlow(1)

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            currentMoviesPage
                .flatMapLatest { repository.getRecommendedMovies(it) }
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            recommendedMovies.addAll(resource.value)
                            _isLoading.value = false
                        }
                        is Resource.Error -> {
                            _errorChannel.send(resource.error)
                            _isLoading.value = false
                        }
                        is Resource.Loading -> {
                            _isLoading.value = true
                        }
                    }
                }
        }
    }

    fun fetchMoreRecommendedMovies() {
        if (!isLoading.value) {
            currentMoviesPage.value += 1
        }
    }
}