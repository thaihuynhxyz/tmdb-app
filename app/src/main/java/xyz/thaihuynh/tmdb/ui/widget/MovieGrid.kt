package xyz.thaihuynh.tmdb.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import xyz.thaihuynh.tmdb.data.Movie

@Composable
fun MovieGrid(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<Movie>,
    navigateToPlayer: (Int) -> Unit,
) {
    when (items.loadState.refresh) {
        is LoadState.Loading -> {
            LinearProgressIndicator()
        }
        is LoadState.Error -> {
            val message =
                (items.loadState.refresh as? LoadState.Error)?.error?.message ?: return

            ErrorScreen(
                message = message,
                modifier = Modifier.fillMaxSize(),
                refresh = { items.retry() }
            )
        }
        else -> {
            LazyVerticalGrid(
                modifier = modifier,
                columns = GridCells.Adaptive(minSize = 256.dp),
                contentPadding = PaddingValues(0.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                content = {
                    items(items.itemCount) { index ->
                        val tmdbItem = items[index]
                        tmdbItem?.let {
                            MovieItem(
                                it,
                                navigateToPlayer,
                                Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }

                    when (items.loadState.append) {
                        is LoadState.Loading -> {
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                LoadingRow(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }

                        is LoadState.Error -> {
                            val message =
                                (items.loadState.append as? LoadState.Error)?.error?.message
                                    ?: return@LazyVerticalGrid

                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                ErrorScreen(
                                    message = message,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    refresh = { items.retry() },
                                )
                            }
                        }

                        else -> {}
                    }
                }
            )
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .width(256.dp)
                .height(384.dp)
                .align(Alignment.Center)
                .clickable { onClick.invoke(movie.id) },
            shape = MaterialTheme.shapes.medium,
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/w500/${movie.posterPath}")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxSize()
                )

                MovieInfo(
                    item = movie,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun MovieInfo(item: Movie, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = item.title ?: "N/A",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
        item.releaseDate?.let {
            Row {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        item.voteAverage?.let {
            RatingBar(
                value = it.toFloat() / 2,
                style = RatingBarStyle.Fill(),
                onValueChange = {},
                onRatingChanged = {},
            )
        }
    }
}