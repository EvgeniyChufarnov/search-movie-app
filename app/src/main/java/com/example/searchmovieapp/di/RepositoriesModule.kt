package com.example.searchmovieapp.di

import com.example.searchmovieapp.data.local.FavoritesDao
import com.example.searchmovieapp.data.local.MoviesDao
import com.example.searchmovieapp.data.remote.MovieDetailsService
import com.example.searchmovieapp.data.remote.MoviesService
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MoviesInteractor
import com.example.searchmovieapp.domain.interactors.impls.MoviesInteractorImpl
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import com.example.searchmovieapp.domain.repositories.MovieDetailsRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import com.example.searchmovieapp.domain.repositories.impls.FavoritesRepositoryImpl
import com.example.searchmovieapp.domain.repositories.impls.LocalMoviesRepositoryImpl
import com.example.searchmovieapp.domain.repositories.impls.MovieDetailsRepositoryImpl
import com.example.searchmovieapp.domain.repositories.impls.MoviesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoriesModule {
    @Provides
    fun providesBaseUrl(): String = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    fun providesLocalMoviesRepository(moviesDao: MoviesDao): LocalMoviesRepository =
        LocalMoviesRepositoryImpl(moviesDao)

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoviesService(retrofit: Retrofit): MoviesService =
        retrofit.create(MoviesService::class.java)

    @Provides
    @Singleton
    fun provideMovieDetailsService(retrofit: Retrofit): MovieDetailsService =
        retrofit.create(MovieDetailsService::class.java)

    @Provides
    @Singleton
    fun provideMoviesRepository(
        moviesService: MoviesService,
    ): MoviesRepository =
        MoviesRepositoryImpl(moviesService)

    @Provides
    @Singleton
    fun provideMovieDetailsRepository(
        movieDetailsService: MovieDetailsService
    ): MovieDetailsRepository =
        MovieDetailsRepositoryImpl(movieDetailsService)

    @Provides
    @Singleton
    fun provideFavoritesRepository(favoritesDao: FavoritesDao): FavoritesRepository =
        FavoritesRepositoryImpl(favoritesDao)
}