package xyz.thaihuynh.tmdb.feature.detail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.layout.DisplayFeature
import coil.compose.AsyncImage
import coil.request.ImageRequest
import xyz.thaihuynh.tmdb.R
import xyz.thaihuynh.tmdb.data.Movie
import xyz.thaihuynh.tmdb.data.Resource
import xyz.thaihuynh.tmdb.ui.widget.ErrorScreen
import xyz.thaihuynh.tmdb.ui.widget.Linkify
import xyz.thaihuynh.tmdb.ui.widget.LoadingRow
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

@Composable
fun DetailMovie(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    viewModel: DetailViewModel = hiltViewModel(),
    onUpPress: () -> Unit = { }
) {
    when (val resource = viewModel.stateFlow.collectAsState().value) {
        is Resource.Loading -> LoadingRow()
        is Resource.Success -> Content(modifier, resource.data, onUpPress)
        is Resource.Error -> ErrorScreen(
            message = resource.message, Modifier.fillMaxSize(),
            refresh = viewModel::refresh
        )
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    movie: Movie,
    upPress: () -> Unit = { }
) {
    Box(modifier = modifier.fillMaxSize()) {
        val scroll = rememberScrollState()
        Header(modifier, movie.backdropPath)
        Body(scroll = scroll, movie = movie)
        Title(movie = movie) { scroll.value }
        Image(imageUrl = "https://image.tmdb.org/t/p/w500/${movie.posterPath}") { scroll.value }
        Up(upPress)
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    backdropPath: String?,
) {
    backdropPath?.let {
        AsyncImage(
            modifier = modifier
                .height(280.dp)
                .fillMaxWidth(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(data = "https://image.tmdb.org/t/p/w500/$backdropPath")
                .crossfade(true)
                .build(),
            contentScale = ContentScale.FillHeight,
            contentDescription = null,
        )
    } ?: Spacer(
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF2be4dc), Color(0xFF243484))))
    )

}

private val gradientScroll = 180.dp
private val minTitleOffset = 56.dp
private val minImageOffset = 12.dp
private val titleHeight = 128.dp
private val imageOverlap = 115.dp
private val hzPadding = Modifier.padding(horizontal = 24.dp)
private val maxTitleOffset = imageOverlap + minTitleOffset + gradientScroll
private val expandedImageSize = 300.dp
private val collapsedImageSize = 150.dp

@Composable
fun Body(
    modifier: Modifier = Modifier,
    scroll: ScrollState,
    movie: Movie
) {
    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(minTitleOffset)
        )
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .padding(bottom = 16.dp)
        ) {
            Spacer(Modifier.height(gradientScroll))
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = getBackgroundColorForElevation(
                            MaterialTheme.colorScheme.background,
                            0.dp
                        ),
                        shape = RectangleShape
                    )
                    .clip(RectangleShape)
            ) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                    Column {
                        Spacer(Modifier.height(imageOverlap))
                        Spacer(Modifier.height(titleHeight))

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.overview),
                            modifier = hzPadding
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = movie.overview ?: "overview",
                            overflow = TextOverflow.Ellipsis,
                            modifier = hzPadding
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home Page",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.width(8.dp))
                            Linkify(
                                movie.homepage ?: "https://www.imdb.com/title/${movie.imdbId}",
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${stringResource(R.string.original_title)}: ",
                            )
                            Text(
                                text = movie.originalTitle ?: "original title",
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${stringResource(R.string.popularity)}: ",
                            )
                            Text(
                                text = "${movie.popularity}",
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${stringResource(R.string.release_date)}: ",
                            )
                            Text(
                                text = "${movie.releaseDate}",
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${stringResource(R.string.runtime)}: ",
                            )
                            Text(
                                text = "${movie.runtime}",
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${stringResource(R.string.status)}: ",
                            )
                            Text(text = "${movie.status}")
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(text = "${stringResource(R.string.video)}: ")
                            Icon(
                                imageVector = if (movie.video == true) Icons.Default.Check else Icons.Default.Clear,
                                contentDescription = "Video",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = hzPadding,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(text = "${stringResource(R.string.vote_count)}: ")
                            Text(text = "${movie.voteCount}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Title(movie: Movie, scrollProvider: () -> Int) {
    val maxOffset = with(LocalDensity.current) { maxTitleOffset.toPx() }
    val minOffset = with(LocalDensity.current) { minTitleOffset.toPx() }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .heightIn(min = titleHeight)
            .statusBarsPadding()
            .offset {
                val scroll = scrollProvider()
                val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
                IntOffset(x = 0, y = offset.toInt())
            }
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = movie.title ?: "title",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = hzPadding
        )
        Text(
            text = movie.tagline ?: "tagline",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = hzPadding
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$${movie.budget}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = hzPadding
        )

        Spacer(Modifier.height(8.dp))
        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
            thickness = 1.dp,
        )
    }
}

@Composable
private fun Image(
    modifier: Modifier = Modifier,
    imageUrl: String,
    scrollProvider: () -> Int
) {
    val collapseRange = with(LocalDensity.current) { (maxTitleOffset - minTitleOffset).toPx() }
    val collapseFractionProvider = {
        (scrollProvider() / collapseRange).coerceIn(0f, 1f)
    }

    CollapsingImageLayout(
        collapseFractionProvider = collapseFractionProvider,
        modifier = hzPadding.then(Modifier.statusBarsPadding())
    ) {
        Box(
            modifier = modifier
                .background(
                    color = getBackgroundColorForElevation(
                        MaterialTheme.colorScheme.background,
                        0.dp
                    ),
                    shape = CircleShape
                )
                .clip(CircleShape)
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.LightGray) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun Up(upPress: () -> Unit) {
    IconButton(
        onClick = upPress,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .size(36.dp)
            .background(
                color = Color.Black.copy(alpha = 0.32f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = stringResource(R.string.back_button)
        )
    }
}

@Composable
private fun CollapsingImageLayout(
    collapseFractionProvider: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurable, constraints ->
        check(measurable.size == 1)

        val collapseFraction = collapseFractionProvider()

        val imageMaxSize = min(expandedImageSize.roundToPx(), constraints.maxWidth)
        val imageMinSize = max(collapsedImageSize.roundToPx(), constraints.minWidth)
        val imageWidth = lerp(imageMaxSize, imageMinSize, collapseFraction)
        val imagePlaceable = measurable[0].measure(Constraints.fixed(imageWidth, imageWidth))

        val imageY = lerp(minTitleOffset, minImageOffset, collapseFraction).roundToPx()
        val imageX = lerp(
            (constraints.maxWidth - imageWidth) / 2, // centered when expanded
            constraints.maxWidth - imageWidth, // right aligned when collapsed
            collapseFraction
        )
        layout(
            width = constraints.maxWidth,
            height = imageY + imageWidth
        ) {
            imagePlaceable.placeRelative(imageX, imageY)
        }
    }
}

@Composable
private fun getBackgroundColorForElevation(color: Color, elevation: Dp): Color {
    return if (elevation > 0.dp) {  // && https://issuetracker.google.com/issues/161429530
        color.withElevation(elevation)
    } else {
        color
    }
}

/**
 * Applies a [Color.White] overlay to this color based on the [elevation]. This increases visibility
 * of elevation for surfaces in a dark theme.
 *
 * TODO: Remove when public https://issuetracker.google.com/155181601
 */
private fun Color.withElevation(elevation: Dp): Color {
    val foreground = calculateForeground(elevation)
    return foreground.compositeOver(this)
}

/**
 * @return the alpha-modified [Color.White] to overlay on top of the surface color to produce
 * the resultant color.
 */
private fun calculateForeground(elevation: Dp): Color {
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return Color.White.copy(alpha = alpha)
}
