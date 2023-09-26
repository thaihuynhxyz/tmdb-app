package xyz.thaihuynh.tmdb.di

import android.content.Context
import androidx.room.Room
import xyz.thaihuynh.tmdb.data.MovieRepository
import xyz.thaihuynh.tmdb.data.api.TmdbService
import xyz.thaihuynh.tmdb.data.db.MovieDao
import xyz.thaihuynh.tmdb.data.db.TmdbDatabase
import xyz.thaihuynh.tmdb.data.db.PageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.thaihuynh.tmdb.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.API_KEY)
                    .build()

                val request = original.newBuilder().url(url).build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    fun provideTmdbService(
        client: OkHttpClient
    ): TmdbService {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbService::class.java)
    }

    @Provides
    fun provideTmdbDatabase(
        @ApplicationContext applicationContext: Context
    ): TmdbDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TmdbDatabase::class.java,
            "tmdb-database"
        ).build()
    }

    @Provides
    fun provideMovieDao(
        database: TmdbDatabase
    ): MovieDao {
        return database.movieDao()
    }

    @Provides
    fun provideTrendingDao(
        database: TmdbDatabase
    ): PageDao {
        return database.trendingDao()
    }

    @Provides
    fun provideMovieRepository(
        apiService: TmdbService,
        movieDao: MovieDao,
        pageDao: PageDao,
    ): MovieRepository {
        return MovieRepository(apiService, movieDao, pageDao)
    }
}