package xyz.thaihuynh.tmdb.feature.trending

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import xyz.thaihuynh.tmdb.ui.widget.MovieGrid

@Composable
fun TrendingMovies(
    modifier: Modifier = Modifier,
    navigateToPlayer: (Int) -> Unit,
    viewModel: TrendingViewModel = hiltViewModel(),
) {
    val items = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    MovieGrid(modifier, items, navigateToPlayer)
}
