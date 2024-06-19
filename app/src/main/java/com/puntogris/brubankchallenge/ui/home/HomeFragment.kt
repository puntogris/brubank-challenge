package com.puntogris.brubankchallenge.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.material.snackbar.Snackbar
import com.puntogris.brubankchallenge.R
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.BlackAndWhiteTransformation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val PAGINATION_COUNTER_TRIGGER = 3

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    HomeScreen(
                        isLoadingState = viewModel.isLoading.collectAsState().value,
                        favoriteMovies = viewModel.favoriteMovies.collectAsState(emptyList()).value,
                        recommendedMovies = viewModel.recommendedMovies,
                        navigateToDetail = ::navigateToDetail,
                        navigateToSearch = ::navigateToSearch,
                        fetchRecommendedMovies = { viewModel.fetchMoreRecommendedMovies() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorChannel.collect { errorRes ->
                Snackbar.make(view, getString(errorRes), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetail(movie: Movie) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(movie)
        findNavController().navigate(action)
    }

    private fun navigateToSearch() {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }
}

@Composable
fun HomeScreen(
    isLoadingState: Boolean,
    favoriteMovies: List<Movie>,
    recommendedMovies: SnapshotStateList<Movie>,
    navigateToDetail: (Movie) -> Unit,
    navigateToSearch: () -> Unit,
    fetchRecommendedMovies: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.4f))
                .height(60.dp)
                .padding(12.dp)
                .clickable { navigateToSearch() },
        ) {
            Icon(
                modifier = Modifier.align(Alignment.BottomStart),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.copy_content_description_search),
                tint = Color.White,
            )
            Text(
                text = stringResource(R.string.copy_movies_reminder),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        Box(modifier = Modifier.height(4.dp)) {
            if (isLoadingState) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                )
            }
        }

        if (favoriteMovies.isNotEmpty()) {
            Section(title = stringResource(R.string.copy_bookmark_movies)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteMovies, key = { it.id }) { movie ->
                        FavouriteCard(
                            movie = movie,
                            onClick = { navigateToDetail(movie) }
                        )
                    }
                }
            }
        }
        val listState = rememberLazyListState()
        val lastVisibleIndex = remember {
            derivedStateOf { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
        }

        LaunchedEffect(key1 = lastVisibleIndex.value) {
            lastVisibleIndex.value?.let {
                if (recommendedMovies.size - it < PAGINATION_COUNTER_TRIGGER) {
                    fetchRecommendedMovies()
                }
            }
        }
        if (recommendedMovies.isNotEmpty()) {
            Section(title = stringResource(R.string.copy_recommended_movies)) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recommendedMovies, key = { it.id }) { movie ->
                        RecommendedCard(
                            movie = movie,
                            onClick = { navigateToDetail(movie) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun FavouriteCard(movie: Movie, onClick: () -> Unit) {
    AsyncImage(
        movie.posterUrl,
        contentDescription = stringResource(R.string.copy_content_description_movie_poster),
        modifier = Modifier
            .size(100.dp, 150.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
fun RecommendedCard(movie: Movie, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .transformations(BlackAndWhiteTransformation())
                .build(),
            imageLoader = ImageLoader.Builder(LocalContext.current).build()
        )
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.copy_content_description_movie_poster),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = movie.title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
        if (movie.primaryGenre.isNotEmpty()) {
            Text(
                text = movie.primaryGenre.uppercase(),
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(6.dp)
            )
        }
    }
}

@Composable
@Preview
fun RecommendedCardPreview() {
    RecommendedCard(
        movie = Movie(
            id = 1,
            title = "The Shawshank Redemption",
            posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
            releaseDate = "1994-09-15",
            overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
            primaryGenre = "Crime",
            isFavorite = false
        ),
        onClick = {}
    )
}

@Composable
@Preview
fun FavouriteCardPreview() {
    FavouriteCard(
        movie = Movie(
            id = 1,
            title = "The Shawshank Redemption",
            posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
            releaseDate = "1994-09-15",
            overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
            primaryGenre = "Crime",
            isFavorite = false
        ),
        onClick = {}
    )
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(
        isLoadingState = false,
        favoriteMovies = listOf(
            Movie(
                id = 1,
                title = "The Shawshank Redemption",
                posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                releaseDate = "1994-09-15",
                overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
                primaryGenre = "Crime",
                isFavorite = false
            ),
            Movie(
                id = 2,
                title = "The Shawshank Redemption",
                posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                releaseDate = "1994-09-15",
                overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
                primaryGenre = "Crime",
                isFavorite = false
            )
        ),
        recommendedMovies = remember {
            mutableStateListOf(
                Movie(
                    id = 1,
                    title = "The Shawshank Redemption",
                    posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                    releaseDate = "1994-09-15",
                    overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
                    primaryGenre = "Crime",
                    isFavorite = false
                ),
                Movie(
                    id = 2,
                    title = "The Shawshank Redemption",
                    posterUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                    backdropUrl = "https://image.tmdb.org/t/p/w500/y9xS5NQTBnFjDoXhSFQeGxlmkoM.jpg",
                    releaseDate = "1994-09-15",
                    overview = "A man is wrongfully convicted of murder and sent to prison. There, he must find a way to clear his name and prove his innocence.",
                    primaryGenre = "Crime",
                    isFavorite = false
                )
            )
        },
        navigateToDetail = {},
        navigateToSearch = {},
        fetchRecommendedMovies = {}
    )
}
