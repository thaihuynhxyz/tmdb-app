package xyz.thaihuynh.tmdb.feature.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import xyz.thaihuynh.tmdb.data.Movie
import xyz.thaihuynh.tmdb.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import androidx.paging.cachedIn

@HiltViewModel
class TrendingViewModel @Inject constructor(
    movieRepository: MovieRepository
) : ViewModel() {

    private val pagerFlow = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = movieRepository::trendingPagingSourceFactory
    ).flow

    val pagingDataFlow: Flow<PagingData<Movie>> = pagerFlow.cachedIn(viewModelScope)
}
