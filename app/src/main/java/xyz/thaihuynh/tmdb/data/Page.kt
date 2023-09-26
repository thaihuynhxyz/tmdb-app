package xyz.thaihuynh.tmdb.data

import androidx.room.Entity
import androidx.room.TypeConverters
import xyz.thaihuynh.tmdb.data.db.TmdbTypeConverters

@Entity(primaryKeys = ["page", "category"])
@TypeConverters(TmdbTypeConverters::class)
data class Page(
    val page: Int,
    val category: String,
    val ids: List<Int>,
    val nextPage: Int?,
)