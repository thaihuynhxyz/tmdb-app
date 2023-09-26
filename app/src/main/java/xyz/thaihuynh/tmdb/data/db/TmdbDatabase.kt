package xyz.thaihuynh.tmdb.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.thaihuynh.tmdb.data.Movie
import xyz.thaihuynh.tmdb.data.Page

@Database(entities = [Movie::class, Page::class], version = 1, exportSchema = false)
abstract class TmdbDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun trendingDao(): PageDao
}