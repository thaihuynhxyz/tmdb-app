package xyz.thaihuynh.tmdb.data.api

import xyz.thaihuynh.tmdb.data.Movie
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Thai
 *
 * This interface is used to define the API endpoints for retrofit
 */
interface TmdbService {
    @GET("/3/trending/movie/day")
    suspend fun getTrendingMovies(@Query("page") page: Int = 1): Paging<Movie>

    @GET("/3/search/movie")
    suspend fun getSearchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Paging<Movie>

    @GET("/3/movie/{id}")
    suspend fun getMovie(@Path("id") id: Int): Movie
}
