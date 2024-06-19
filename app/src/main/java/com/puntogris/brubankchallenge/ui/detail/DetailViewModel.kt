package com.puntogris.brubankchallenge.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val repository: IRepository
) : ViewModel() {

    val currentMovie = stateHandle.getStateFlow<Movie?>("movie", null)

    fun toggleFavoriteState(movie: Movie) {
        viewModelScope.launch {
            if (movie.isFavorite) {
                repository.removeMovieFromFavorites(movie)
            } else {
                repository.addMovieToFavorites(movie)
            }
            stateHandle["movie"] = movie.copy(isFavorite = !movie.isFavorite)
        }
    }
}