package com.example.searchmovieapp.di

import android.content.Context
import androidx.room.Room
import com.example.searchmovieapp.data.local.FavoritesDao
import com.example.searchmovieapp.data.local.FavoritesDatabase
import com.example.searchmovieapp.data.local.MoviesDao
import com.example.searchmovieapp.data.local.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMoviesDatabase(@ApplicationContext appContext: Context): MoviesDatabase {
        return Room.databaseBuilder(
            appContext,
            MoviesDatabase::class.java,
            "movies.db"
        ).build()
    }

    @Provides
    fun provideMoviesDao(database: MoviesDatabase): MoviesDao {
        return database.moviesDao()
    }

    @Provides
    @Singleton
    fun provideFavoritesDatabase(@ApplicationContext appContext: Context): FavoritesDatabase {
        return Room.databaseBuilder(
            appContext,
            FavoritesDatabase::class.java,
            "favorites.db"
        ).build()
    }

    @Provides
    fun provideFavoritesDao(database: FavoritesDatabase): FavoritesDao {
        return database.favoritesDao()
    }
}