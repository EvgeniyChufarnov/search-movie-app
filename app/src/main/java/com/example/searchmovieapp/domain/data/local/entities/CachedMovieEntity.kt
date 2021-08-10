package com.example.searchmovieapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MovieEntityType {
    NOW_PLAYING, UPCOMING, TOP_RATED
}

@Entity(tableName = "movies")
data class CachedMovieEntity(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_path") var posterPath: String?,
    @ColumnInfo(name = "release_date") val releaseDate: String,
    @ColumnInfo(name = "vote_average") val voteAverage: Float,
    @ColumnInfo(name = "is_upcoming") var isUpcoming: Boolean,
    @ColumnInfo(name = "page") var page: Int,
    @ColumnInfo(name = "language") var language: String,
    @ColumnInfo(name = "type") var type: Int,
    @PrimaryKey(autoGenerate = true) val localId: Int = 0
)