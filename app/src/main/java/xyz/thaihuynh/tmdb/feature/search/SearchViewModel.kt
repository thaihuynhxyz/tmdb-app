package xyz.thaihuynh.tmdb.feature.search

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import xyz.thaihuynh.tmdb.data.Movie
import xyz.thaihuynh.tmdb.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    movieRepository: MovieRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var _pagingSource: PagingSource<Int, Movie>? = null
    val pagerFlow = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = {
            _pagingSource = movieRepository.searchPagingSourceFactory(query.value)
            _pagingSource!!
        }
    ).flow

    fun onQueryChanged(s: String) {
        _query.value = s
        _pagingSource?.invalidate()
    }
}