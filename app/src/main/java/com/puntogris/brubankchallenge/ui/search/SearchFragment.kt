package com.puntogris.brubankchallenge.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.google.android.material.snackbar.Snackbar
import com.puntogris.brubankchallenge.R
import com.puntogris.brubankchallenge.domain.models.Movie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    SearchScreen(
                        onClose = { findNavController().popBackStack() },
                        onSearch = { viewModel.updateQuery(it) },
                        onToggleFavoriteState = { viewModel.toggleFavoriteState(it) },
                        onNavigateToDetail = { navigateToDetail(it) },
                        query = viewModel.query.collectAsState().value,
                        results = viewModel.searchResults.collectAsState(emptyList()).value
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
        val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(movie)
        findNavController().navigate(action)
    }
}

@Composable
fun SearchScreen(
    onClose: () -> Unit,
    onSearch: (String) -> Unit,
    onToggleFavoriteState: (Movie) -> Unit,
    onNavigateToDetail: (Movie) -> Unit,
    query: String,
    results: List<Movie>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        SearchMovie(
            query = query,
            onSearch = onSearch,
            onClose = onClose
        )

        Spacer(modifier = Modifier.height(16.dp))

        ResultsMovies(
            results = results,
            onNavigateToDetail = onNavigateToDetail,
            onToggleFavoriteState = onToggleFavoriteState
        )
    }
}

@Composable
fun SearchMovie(query: String, onSearch: (String) -> Unit, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray.copy(alpha = 0.4f))
            .height(60.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .weight(1f)
                .background(Color.DarkGray.copy(alpha = 0.6f))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.copy_content_description_search),
                tint = Color.Gray,
            )
            BasicTextField(
                value = query,
                onValueChange = { onSearch(it) },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.White),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.copy_search),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        innerTextField()
                    }
                }
            )
            Box(
                modifier = Modifier
                    .background(Color.Gray, shape = CircleShape)
                    .clickable { onSearch("") }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = Color.DarkGray,
                    contentDescription = stringResource(R.string.copy_content_description_clear_text),
                    modifier = Modifier
                        .padding(1.dp)
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        }
        Text(
            modifier = Modifier.clickable { onClose() },
            text = stringResource(R.string.copy_cancel),
            color = Color.White
        )
    }
}

@Composable
fun ResultsMovies(
    results: List<Movie>,
    onNavigateToDetail: (Movie) -> Unit,
    onToggleFavoriteState: (Movie) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(results) { movie ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    movie.posterUrl,
                    contentDescription = stringResource(R.string.copy_content_description_movie_poster),
                    modifier = Modifier
                        .size(70.dp, 100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToDetail(movie) }
                )
                Column(
                    modifier = Modifier.weight(1f).clickable { onNavigateToDetail(movie) },
                ){
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (movie.primaryGenre.isNotEmpty()) {
                        Text(
                            text = movie.primaryGenre.uppercase(),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                val buttonText = stringResource(
                    if (movie.isFavorite) R.string.copy_unbookmark else R.string.copy_bookmark
                )
                val buttonBg = if (movie.isFavorite) Color.DarkGray else Color.Transparent

                OutlinedButton(
                    modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = buttonBg,
                    ),
                    shape = RoundedCornerShape(6.dp),
                    onClick = { onToggleFavoriteState(movie) }
                ) {
                    Text(
                        text = buttonText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(thickness = 1.dp, color = Color.DarkGray)
        }
    }
}

@Composable
@Preview
fun SearchMoviePreview() {
    SearchScreen(
        query = "The Shawshank Redemption",
        onSearch = {},
        onClose = {},
        onToggleFavoriteState = {},
        onNavigateToDetail = {},
        results = listOf(
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
                isFavorite = true
            )
        )
    )
}