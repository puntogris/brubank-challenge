package com.puntogris.brubankchallenge.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEBOUNCE_DELAY_MILLIS = 300L

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: IRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Movie>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _errorChannel = Channel<Int>()
    val errorChannel = _errorChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            _query
                .debounce(DEBOUNCE_DELAY_MILLIS)
                .transformLatest { query -> emit(repository.searchMovies(query)) }
                .collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            _searchResults.value = resource.value
                        }

                        is Resource.Error -> {
                            _errorChannel.send(resource.error)
                        }

                        is Resource.Loading -> {}
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _query.value = query
    }

    fun toggleFavoriteState(updatedMovie: Movie) {
        viewModelScope.launch {
            if (updatedMovie.isFavorite) {
                repository.removeMovieFromFavorites(updatedMovie)
            } else {
                repository.addMovieToFavorites(updatedMovie)
            }
            _searchResults.update { movies ->
                movies.map { movie ->
                    if (movie.id == updatedMovie.id) {
                        movie.copy(isFavorite = !movie.isFavorite)
                    } else {
                        movie
                    }
                }
            }
        }
    }
}