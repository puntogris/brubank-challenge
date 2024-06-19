package com.puntogris.brubankchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.ui.search.SearchViewModel
import com.puntogris.brubankchallenge.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: IRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `updateQuery should update the query state`() = runBlockingTest {
        val query = "Test Query"

        viewModel.updateQuery(query)

        assertEquals(query, viewModel.query.value)
    }

    @Test
    fun `search results should update correctly`() = runTest {
        val query = "Movie"
        val expectedMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                backdropUrl = "",
                posterUrl = "",
                overview = "",
                releaseDate = "",
                primaryGenre = "",
                isFavorite = false
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                backdropUrl = "",
                posterUrl = "",
                overview = "",
                releaseDate = "",
                primaryGenre = "",
                isFavorite = true
            )
        )

        val expectedResults = Resource.Success(expectedMovies)
        `when`(repository.searchMovies(query)).thenReturn(expectedResults)

        viewModel.updateQuery(query)
        advanceUntilIdle()

        assertEquals(expectedMovies, viewModel.searchResults.value)
    }

    @Test
    fun `toggleFavoriteState should update favourite movie state and search results`() = runTest {
        val movie = Movie(
            id = 1,
            title = "Movie 1",
            backdropUrl = "",
            posterUrl = "",
            overview = "",
            releaseDate = "",
            primaryGenre = "",
            isFavorite = false
        )
        val expectedUpdatedMovie = movie.copy(isFavorite = true)

        `when`(repository.searchMovies("")).thenReturn(Resource.Success(listOf(movie)))
        `when`(repository.addMovieToFavorites(movie)).then { movie.copy(isFavorite = true) }
        `when`(repository.removeMovieFromFavorites(movie)).then { movie.copy(isFavorite = false) }

        advanceUntilIdle()
        viewModel.toggleFavoriteState(movie)
        advanceUntilIdle()

        assertEquals(
            expectedUpdatedMovie.isFavorite,
            viewModel.searchResults.value.first().isFavorite
        )
    }
}
