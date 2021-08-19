package com.example.searchmovieapp.di

import android.content.Context
import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MovieDetailsInteractor
import com.example.searchmovieapp.domain.interactors.MoviesInteractor
import com.example.searchmovieapp.domain.interactors.WorkInteractor
import com.example.searchmovieapp.domain.interactors.impls.FavoritesInteractorImpl
import com.example.searchmovieapp.domain.interactors.impls.MovieDetailsInteractorImpl
import com.example.searchmovieapp.domain.interactors.impls.MoviesInteractorImpl
import com.example.searchmovieapp.domain.interactors.impls.WorkInteractorImpl
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import com.example.searchmovieapp.domain.repositories.LocalMoviesRepository
import com.example.searchmovieapp.domain.repositories.MovieDetailsRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class InteractorsModule {
    @Provides
    @Singleton
    fun provideMoviesInteractor(
        moviesRepository: MoviesRepository,
        localMoviesRepository: LocalMoviesRepository,
        favoritesInteractor: FavoritesInteractor,
        workInteractor: WorkInteractor
    ): MoviesInteractor =
        MoviesInteractorImpl(
            moviesRepository,
            localMoviesRepository,
            favoritesInteractor,
            workInteractor,
            Locale.getDefault().language
        )

    @Provides
    @Singleton
    fun provideMovieDetailsInteractor(
        movieDetailsRepository: MovieDetailsRepository,
        favoritesInteractor: FavoritesInteractor
    ): MovieDetailsInteractor =
        MovieDetailsInteractorImpl(
            movieDetailsRepository,
            favoritesInteractor,
            Locale.getDefault().language
        )

    @Provides
    @Singleton
    fun provideFavoritesInteractor(
        favoritesRepository: FavoritesRepository
    ): FavoritesInteractor =
        FavoritesInteractorImpl(favoritesRepository)

    @Provides
    @Singleton
    fun provideWorkInteractor(
        @ApplicationContext applicationContext: Context
    ): WorkInteractor =
        WorkInteractorImpl(applicationContext)
}