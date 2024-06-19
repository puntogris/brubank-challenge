package com.puntogris.brubankchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.ui.detail.DetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: IRepository

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = SavedStateHandle()
        viewModel = DetailViewModel(savedStateHandle, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `toggleFavoriteState should add movie to favorites`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            backdropUrl = "",
            posterUrl = "",
            overview = "",
            releaseDate = "",
            primaryGenre = "",
            isFavorite = false
        )
        savedStateHandle["movie"] = movie

        viewModel.toggleFavoriteState(movie)

        verify(repository).addMovieToFavorites(movie)
        verify(repository, never()).removeMovieFromFavorites(movie)
        assert(savedStateHandle.get<Movie>("movie")?.isFavorite == true)
    }

    @Test
    fun `toggleFavoriteState should remove movie from favorites`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            backdropUrl = "",
            posterUrl = "",
            overview = "",
            releaseDate = "",
            primaryGenre = "",
            isFavorite = true
        )
        savedStateHandle["movie"] = movie

        viewModel.toggleFavoriteState(movie)

        verify(repository).removeMovieFromFavorites(movie)
        verify(repository, never()).addMovieToFavorites(movie)
        assert(savedStateHandle.get<Movie>("movie")?.isFavorite == false)
    }
}
