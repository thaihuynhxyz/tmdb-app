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
    val adult: Boolean? = null,
    val budget: Int? = null,
    val revenue: Int? = null,
    val runtime: Int? = null,
    val status: String? = null,
    val tagline: String? = null,
    val homepage: String? = null,
    @ColumnInfo(name = "imdb_id")
    @field:SerializedName("imdb_id")
    val imdbId: String? = null,
    @ColumnInfo(name = "backdrop_path")
    @field:SerializedName("backdrop_path")
    val backdropPath: String? = null,
    val title: String? = null,
    @ColumnInfo(name = "original_language")
    @field:SerializedName("original_language")
    val originalLanguage: String? = null,
    @ColumnInfo(name = "original_title")
    @field:SerializedName("original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    @ColumnInfo(name = "poster_path")
    @field:SerializedName("poster_path")
    val posterPath: String? = null,
    @ColumnInfo(name = "media_type")
    @field:SerializedName("media_type")
    val mediaType: String? = null,
    @ColumnInfo(name = "genre_ids")
    @field:SerializedName("genre_ids")
    val genreIds: List<Int>? = null,
    val popularity: Double? = null,
    @ColumnInfo(name = "release_date")
    @field:SerializedName("release_date")
    val releaseDate: String? = null,
    val video: Boolean? = null,
    @ColumnInfo(name = "vote_average")
    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,
    @ColumnInfo(name = "vote_count")
    @field:SerializedName("vote_count")
    val voteCount: Int? = null,
)