package com.example.searchmovieapp.di

import com.example.searchmovieapp.domain.interactors.FavoritesInteractor
import com.example.searchmovieapp.domain.interactors.MovieDetailsInteractor
import com.example.searchmovieapp.domain.interactors.MoviesInteractor
import com.example.searchmovieapp.ui.details.MovieDetailsContract
import com.example.searchmovieapp.ui.details.MovieDetailsPresenter
import com.example.searchmovieapp.ui.favorites.FavoritesContract
import com.example.searchmovieapp.ui.favorites.FavoritesPresenter
import com.example.searchmovieapp.ui.home.HomeContract
import com.example.searchmovieapp.ui.home.HomePresenter
import com.example.searchmovieapp.ui.ratings.RatingsContract
import com.example.searchmovieapp.ui.ratings.RatingsPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PresentersModule {
    @Singleton
    @Provides
    fun provideHomePresenter(
        moviesInteractor: MoviesInteractor,
        favoritesInteractor: FavoritesInteractor
    ): HomeContract.Presenter {
        return HomePresenter(moviesInteractor, favoritesInteractor)
    }

    @Singleton
    @Provides
    fun provideFavoritesPresenter(favoritesInteractor: FavoritesInteractor): FavoritesContract.Presenter {
        return FavoritesPresenter(favoritesInteractor)
    }

    @Singleton
    @Provides
    fun provideRatingsPresenter(
        moviesInteractor: MoviesInteractor,
        favoritesInteractor: FavoritesInteractor
    ): RatingsContract.Presenter {
        return RatingsPresenter(moviesInteractor, favoritesInteractor)
    }

    @Provides
    fun provideMovieDetailsPresenter(
        movieDetailsInteractor: MovieDetailsInteractor,
        favoritesInteractor: FavoritesInteractor
    ): MovieDetailsContract.Presenter {
        return MovieDetailsPresenter(movieDetailsInteractor, favoritesInteractor)
    }
}