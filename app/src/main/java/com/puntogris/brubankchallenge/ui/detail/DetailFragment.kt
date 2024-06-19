package com.puntogris.brubankchallenge.ui.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.puntogris.brubankchallenge.R
import com.puntogris.brubankchallenge.domain.models.Movie
import com.puntogris.brubankchallenge.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint

private const val MIN_SCROLL_ALPHA_PERCENTAGE = 0.3f
private const val MAX_SCROLL_ALPHA_PERCENTAGE = 1f
private const val DELTA_SCROLL_ALPHA_PERCENTAGE = 800

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    DetailScreen(
                        movie = viewModel.currentMovie.collectAsState().value,
                        onToggleFavoriteState = { viewModel.toggleFavoriteState(it) },
                        onNavigateBack = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    movie: Movie?,
    onToggleFavoriteState: (Movie) -> Unit,
    onNavigateBack: () -> Unit
) {
    var backgroundColor by remember { mutableStateOf(Color.Black) }
    val scrollState = rememberScrollState()
    val scrollAlphaPercentage = (1 - scrollState.value.toFloat() / DELTA_SCROLL_ALPHA_PERCENTAGE)
        .coerceIn(MIN_SCROLL_ALPHA_PERCENTAGE, MAX_SCROLL_ALPHA_PERCENTAGE)

    if (movie == null) {
        onNavigateBack()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = movie.backdropUrl,
            contentDescription = stringResource(R.string.copy_content_description_background_image),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor.copy(alpha = 0.8f))
        )
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black, shape = CircleShape)
                .align(Alignment.TopStart)
                .clickable { onNavigateBack() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                tint = Color.White,
                contentDescription = stringResource(R.string.copy_content_description_navigate_back),
                modifier = Modifier
                    .padding(6.dp)
                    .size(24.dp)
                    .align(Alignment.Center),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadImageWithDominantColor(
                posterUrl = movie.posterUrl,
                scrollState,
                onDominantColorChange = { backgroundColor = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.alpha(scrollAlphaPercentage),
                text = movie.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.alpha(scrollAlphaPercentage),
                text = DateUtils.getYearFromDate(movie.releaseDate),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.alpha(scrollAlphaPercentage),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White
                ),
                onClick = { onToggleFavoriteState(movie) }) {
                Text(
                    text = stringResource(
                        if (movie.isFavorite) R.string.copy_unbookmark else R.string.copy_bookmark
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(R.string.copy_overview),
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.overview,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun LoadImageWithDominantColor(
    posterUrl: String,
    scrollState: ScrollState,
    onDominantColorChange: (Color) -> Unit
) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    LaunchedEffect(posterUrl) {
        val request = ImageRequest.Builder(context)
            .data(posterUrl)
            .target { drawable ->
                imageBitmap = drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
                imageBitmap?.let {
                    val dominantColor = Palette.from(it).generate().dominantSwatch?.rgb
                    if (dominantColor != null) {
                        onDominantColorChange(Color(dominantColor))
                    }
                }
            }
            .build()

        ImageLoader(context).execute(request)
    }
    AsyncImage(
        modifier = Modifier
            .size(250.dp, 350.dp)
            .clip(RoundedCornerShape(8.dp))
            .graphicsLayer {
                val scale = (1f - (scrollState.value.toFloat() / 800)).coerceAtLeast(0.5f)
                val translation = scrollState.value.toFloat() / 2
                scaleX = scale
                scaleY = scale
                translationY = translation
            },
        model = imageBitmap,
        contentDescription = stringResource(R.string.copy_content_description_movie_poster),
        contentScale = ContentScale.Crop
    )
}

@Composable
@Preview
fun DetailScreenPreview() {
    DetailScreen(
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
        onToggleFavoriteState = {},
        onNavigateBack = {}
    )
}