package xyz.thaihuynh.tmdb.data.api

import com.google.gson.annotations.SerializedName

data class Paging<T>(
    val page: Int,
    val results: List<T>,
    @field:SerializedName("total_pages")
    val totalPages: Int,
    @field:SerializedName("total_results")
    val totalResults: Int
)