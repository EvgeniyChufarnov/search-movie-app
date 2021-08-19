package com.example.searchmovieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.searchmovieapp.data.local.entities.CachedFavoriteMovieEntity

@Database(entities = [CachedFavoriteMovieEntity::class], version = 1)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}