package xyz.thaihuynh.tmdb.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import xyz.thaihuynh.tmdb.R
import xyz.thaihuynh.tmdb.ui.widget.MovieGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovies(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    navigateToPlayer: (Int) -> Unit,
    isOffline: Boolean = false,
) {

    var active by remember { mutableStateOf(false) }
    val items = viewModel.pagerFlow.collectAsLazyPagingItems()
    val query by viewModel.query.collectAsState()

    DockedSearchBar(
        modifier = if (active) modifier.fillMaxSize() else modifier.fillMaxWidth(),
        query = query,
        onQueryChange = viewModel::onQueryChanged,
        onSearch = { active = false },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = { Text(text = stringResource(id = R.string.search_movies)) },
        shape = SearchBarDefaults.fullScreenShape,
        enabled = !isOffline,
        leadingIcon = {
            if (active) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button),
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable {
                            active = false
                            viewModel.onQueryChanged("")
                        },
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search),
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        },
    ) {
        MovieGrid(
            modifier = Modifier.fillMaxSize(),
            items = items,
            navigateToPlayer = navigateToPlayer,
        )
    }
}