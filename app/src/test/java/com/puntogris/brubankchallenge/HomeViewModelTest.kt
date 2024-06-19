package com.puntogris.brubankchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.ui.home.HomeViewModel
import com.puntogris.brubankchallenge.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: IRepository

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        `when`(repository.getFavoriteMovies()).thenReturn(
            flowOf(
                listOf(
                    Movie(
                        id = 1,
                        title = "Favorite Movie",
                        backdropUrl = "",
                        posterUrl = "",
                        overview = "",
                        releaseDate = "",
                        primaryGenre = "",
                        isFavorite = true
                    )
                )
            )
        )
        runTest {
            `when`(repository.getRecommendedMovies(1)).thenReturn(
                flow {
                    emit(
                        Resource.Success(
                            listOf(
                                Movie(
                                    id = 1,
                                    title = "Recommended Movie",
                                    backdropUrl = "",
                                    posterUrl = "",
                                    overview = "",
                                    releaseDate = "",
                                    primaryGenre = "",
                                    isFavorite = false
                                )
                            )
                        )
                    )
                }
            )
        }
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `favoriteMovies should emit favorite movies from repository`() = runTest {
        val favoriteMovies = viewModel.favoriteMovies.first()
        assert(favoriteMovies.isNotEmpty())
        assert(favoriteMovies[0].title == "Favorite Movie")
    }

    @Test
    fun `recommendedMovies should emit recommended movies from repository`() = runTest {
        val recommendedMovies = viewModel.recommendedMovies.toList()
        assert(recommendedMovies.isNotEmpty())
        assert(recommendedMovies[0].title == "Recommended Movie")
    }
}
