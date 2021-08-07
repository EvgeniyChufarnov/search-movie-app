package com.example.searchmovieapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.searchmovieapp.data.local.entities.CachedMovieEntity

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies WHERE page == :page AND language == :language AND type == :type")
    fun getMoviesByPage(page: Int, language: String, type: Int): List<CachedMovieEntity>

    @Query("SELECT * FROM movies WHERE language == :language AND type == :type")
    fun getMovies(language: String, type: Int): List<CachedMovieEntity>

    @Insert
    fun addMovies(vararg movies: CachedMovieEntity)

    @Query("DELETE FROM movies")
    fun clear()
}