package com.example.searchmovieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.searchmovieapp.data.local.entities.CachedMovieEntity

@Database(entities = [CachedMovieEntity::class], version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}