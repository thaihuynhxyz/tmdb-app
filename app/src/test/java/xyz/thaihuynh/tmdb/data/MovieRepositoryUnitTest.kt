package xyz.thaihuynh.tmdb.data

import android.content.SharedPreferences
import androidx.paging.PagingSource
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.times
import xyz.thaihuynh.tmdb.data.api.Paging
import xyz.thaihuynh.tmdb.data.api.TmdbService
import xyz.thaihuynh.tmdb.data.db.MovieDao
import xyz.thaihuynh.tmdb.data.db.PageDao

@RunWith(JUnit4::class)
class MovieRepositoryUnitTest {

    private lateinit var movieRepository: MovieRepository

    private val service: TmdbService = mock(TmdbService::class.java)
    private val movieDao: MovieDao = mock(MovieDao::class.java)
    private val pageDao: PageDao = mock(PageDao::class.java)
    private val sharedPreferences: SharedPreferences = mock(SharedPreferences::class.java)
    private val editor: SharedPreferences.Editor = mock(SharedPreferences.Editor::class.java)

    @Before
    fun setUp() {
        movieRepository = MovieRepository(service, movieDao, pageDao, sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
    }

    @Test
    fun getTrendingMovies_ServiceSuccess_FirstCache() = runTest {
        val result = Paging(1, listOf(Movie(1)), 1, 1)
        `when`(service.getTrendingMovies(1)).thenReturn(result)
        `when`(sharedPreferences.getLong("last_cache", 0)).thenReturn(0)

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(pageDao).deleteAll()
        verify(pageDao).insert(eq(Page(1, "trending", listOf(1), null)))
        verify(editor).putLong(eq("last_cache"), any())
        verify(movieDao).insertAll(eq(result.results))
    }

    @Test
    fun getTrendingMovies_ServiceSuccess_DeleteCacheWhenFirstPage() = runTest {
        val result = Paging(1, listOf(Movie(1)), 1, 1)
        `when`(service.getTrendingMovies(1)).thenReturn(result)
        `when`(sharedPreferences.getLong("last_cache", 0)).thenReturn(System.currentTimeMillis())

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(pageDao).insert(eq(Page(1, "trending", listOf(1), null)))
        verify(editor).putLong(eq("last_cache"), any())
        verify(movieDao).insertAll(eq(result.results))
    }

    @Test
    fun getTrendingMovies_ServiceSuccess_DeleteCacheAfter1Day() = runTest {
        val result = Paging(1, listOf(Movie(1)), 1, 1)
        `when`(service.getTrendingMovies(1)).thenReturn(result)
        `when`(sharedPreferences.getLong("last_cache", 0)).thenReturn(System.currentTimeMillis() - 24 * 60 * 60 * 1000)

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(pageDao).deleteAll()
        verify(pageDao).insert(eq(Page(1, "trending", listOf(1), null)))
        verify(editor).putLong(eq("last_cache"), any())
        verify(movieDao).insertAll(eq(result.results))
    }

    @Test
    fun getTrendingMovies_ServiceSuccess_NextPage() = runTest {
        val result1 = Paging(1, listOf(Movie(1)), 2, 2)
        val result2 = Paging(2, listOf(Movie(2)), 2, 2)
        `when`(service.getTrendingMovies(1)).thenReturn(result1)
        `when`(service.getTrendingMovies(2)).thenReturn(result2)
        `when`(sharedPreferences.getLong("last_cache", 0)).thenReturn(System.currentTimeMillis() - 24 * 60 * 60 * 1000)

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(pageDao).insert(eq(Page(1, "trending", listOf(1), 2)))
        verify(editor).putLong(eq("last_cache"), any())
        verify(movieDao).insertAll(eq(result1.results))

        val loadResult2 = movieRepository.getTrendingMovies(2)
        assert(loadResult2 is PagingSource.LoadResult.Page)
        verify(pageDao).insert(eq(Page(2, "trending", listOf(2), null)))
        verify(editor, times(2)).putLong(eq("last_cache"), any())
        verify(movieDao).insertAll(eq(result2.results))
    }

    @Test
    fun getTrendingMovies_ServiceFailed_HaveCache() = runTest {
        val exception = Exception()
        given(service.getTrendingMovies(1)).willAnswer {
            throw exception
        }
        val trending = Page(1, "trending", listOf(1), 2)
        `when`(pageDao.getTrendingByPageAndCategory(1, "trending")).thenReturn(trending)
        val movie = Movie(1)
        `when`(movieDao.getMoviesByIds(listOf(1))).thenReturn(listOf(movie))

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(pageDao).getTrendingByPageAndCategory(1, "trending")
        verify(movieDao).getMoviesByIds(listOf(1))
    }

    @Test
    fun getTrendingMovies_ServiceFailed_NoCache() = runTest {
        val exception = Exception()
        given(service.getTrendingMovies(1)).willAnswer {
            throw exception
        }
        `when`(pageDao.getTrendingByPageAndCategory(1, "trending")).thenReturn(null)

        val loadResult = movieRepository.getTrendingMovies(1)

        assert(loadResult is PagingSource.LoadResult.Error)
        verify(pageDao).getTrendingByPageAndCategory(1, "trending")
    }

    @Test
    fun getSearchMovies_ServiceSuccess() = runTest {
        val result = Paging(1, listOf(Movie(1)), 1, 1)
        `when`(service.getSearchMovies("query", 1)).thenReturn(result)

        val loadResult = movieRepository.getSearchMovies("query", 1)

        assert(loadResult is PagingSource.LoadResult.Page)
        verify(movieDao).insertAll(eq(result.results))
    }

    @Test
    fun getSearchMovies_ServiceFailed() = runTest {
        val exception = Exception()
        given(service.getSearchMovies("query", 1)).willAnswer {
            throw exception
        }

        val loadResult = movieRepository.getSearchMovies("query", 1)

        assert(loadResult is PagingSource.LoadResult.Error)
    }
}