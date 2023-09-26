package xyz.thaihuynh.tmdb.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.thaihuynh.tmdb.data.Page

@Dao
interface PageDao {
    @Query("SELECT * FROM page WHERE page = :page AND category = :category")
    suspend fun getTrendingByPageAndCategory(page: Int, category: String): Page?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trending: Page)

    @Query("DELETE FROM page")
    suspend fun deleteAll()
}