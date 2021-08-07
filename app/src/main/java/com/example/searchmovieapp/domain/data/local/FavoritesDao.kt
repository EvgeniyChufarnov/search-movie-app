package com.example.searchmovieapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.searchmovieapp.data.local.entities.CachedFavoriteMovieEntity

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites")
    fun getFavoritesMovies(): List<CachedFavoriteMovieEntity>

    @Insert
    fun addToFavorites(movie: CachedFavoriteMovieEntity)

    @Delete
    fun removeFromFavorites(movie: CachedFavoriteMovieEntity)
}