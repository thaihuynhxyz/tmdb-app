package xyz.thaihuynh.tmdb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import xyz.thaihuynh.tmdb.data.db.TmdbTypeConverters

@Entity
@TypeConverters(TmdbTypeConverters::class)
data class Movie(
    @PrimaryKey val id: Int,
    val adult: Boolean?,
    val budget: Int?,
    val revenue: Int?,
    val runtime: Int?,
    val status: String?,
    val tagline: String?,
    val homepage: String?,
    @ColumnInfo(name = "imdb_id")
    @field:SerializedName("imdb_id")
    val imdbId: String?,
    @ColumnInfo(name = "backdrop_path")
    @field:SerializedName("backdrop_path")
    val backdropPath: String?,
    val title: String?,
    @ColumnInfo(name = "original_language")
    @field:SerializedName("original_language")
    val originalLanguage: String?,
    @ColumnInfo(name = "original_title")
    @field:SerializedName("original_title")
    val originalTitle: String?,
    val overview: String?,
    @ColumnInfo(name = "poster_path")
    @field:SerializedName("poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "media_type")
    @field:SerializedName("media_type")
    val mediaType: String?,
    @ColumnInfo(name = "genre_ids")
    @field:SerializedName("genre_ids")
    val genreIds: List<Int>?,
    val popularity: Double?,
    @ColumnInfo(name = "release_date")
    @field:SerializedName("release_date")
    val releaseDate: String?,
    val video: Boolean?,
    @ColumnInfo(name = "vote_average")
    @field:SerializedName("vote_average")
    val voteAverage: Double?,
    @ColumnInfo(name = "vote_count")
    @field:SerializedName("vote_count")
    val voteCount: Int?
)