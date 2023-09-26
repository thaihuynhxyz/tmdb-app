package xyz.thaihuynh.tmdb.data

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import xyz.thaihuynh.tmdb.data.api.TmdbService
import xyz.thaihuynh.tmdb.data.db.MovieDao
import xyz.thaihuynh.tmdb.data.db.PageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val tmdbService: TmdbService,
    private val movieDao: MovieDao,
    private val pageDao: PageDao,
) {

    /**
     * Get trending movies from the api and cache them in the database. In offline mode, the
     * cached movies will be returned.
     *
     * @return a flow of trending movies
     */
    suspend fun getTrendingMovies(page: Int = 1): LoadResult<Int, Movie> {
        try {
            val paging = tmdbService.getTrendingMovies(page)
            val nextPage = if (paging.page == paging.totalPages) null else paging.page + 1
            if (page == 1) {
                pageDao.deleteAll()
            }
            pageDao.insert(
                Page(
                    paging.page,
                    "trending",
                    paging.results.map { it.id },
                    nextPage,
                )
            )
            movieDao.insertAll(paging.results)
            return LoadResult.Page(paging.results, null, nextPage)
        } catch (e: Exception) {
            e.printStackTrace()
            val trending = pageDao.getTrendingByPageAndCategory(page, "trending")
            return if (trending != null) {
                val movies = movieDao.getMoviesByIds(trending.ids)
                LoadResult.Page(movies, null, trending.nextPage)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    suspend fun getSearchMovies(query: String, page: Int = 1): LoadResult<Int, Movie> {
        return try {
            val paging = tmdbService.getSearchMovies(query, page)
            val nextPage = if (paging.page == paging.totalPages) null else paging.page + 1
            movieDao.insertAll(paging.results)
            LoadResult.Page(paging.results, null, nextPage)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    fun trendingPagingSourceFactory(): PagingSource<Int, Movie> {
        return TrendingPagingSource(this)
    }

    fun searchPagingSourceFactory(query: String): PagingSource<Int, Movie> {
        return SearchPagingSource(this, query)
    }

    fun getMovie(id: Int): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading)
        try {
            val movie = tmdbService.getMovie(id)
            movieDao.insert(movie)
            emit(Resource.Success(movie))
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                movieDao.getMovieById(id)?.let {
                    emit(Resource.Success(it))
                } ?: run {
                    emit(Resource.Error("Failed to load movie"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message ?: "Failed to load movie"))
            }
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        private class TrendingPagingSource(
            private val repository: MovieRepository,
        ) : PagingSource<Int, Movie>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
                return repository.getTrendingMovies(params.key ?: 1)
            }

            override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
                return state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.nextKey?.minus(1)
                }
            }
        }

        private class SearchPagingSource(
            private val repository: MovieRepository,
            private val query: String,
        ) : PagingSource<Int, Movie>() {

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
                if (query.isEmpty()) {
                    return LoadResult.Page(emptyList(), null, null)
                }
                return repository.getSearchMovies(query, params.key ?: 1)
            }

            override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
                return state.anchorPosition?.let {
                    state.closestPageToPosition(it)?.nextKey?.minus(1)
                }
            }
        }
    }
}
