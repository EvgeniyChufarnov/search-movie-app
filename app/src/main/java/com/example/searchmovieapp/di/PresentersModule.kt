package com.example.searchmovieapp.di

import com.example.searchmovieapp.repositories.FavoritesRepository
import com.example.searchmovieapp.repositories.MovieDetailsRepository
import com.example.searchmovieapp.repositories.MoviesRepository
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
class PresentersModule {
    @Singleton
    @Provides
    fun provideHomePresenter(moviesRepository: MoviesRepository): HomeContract.Presenter {
        return HomePresenter(moviesRepository)
    }

    @Singleton
    @Provides
    fun provideFavoritesPresenter(favoritesRepository: FavoritesRepository): FavoritesContract.Presenter {
        return FavoritesPresenter(favoritesRepository)
    }

    @Singleton
    @Provides
    fun provideRatingsPresenter(moviesRepository: MoviesRepository): RatingsContract.Presenter {
        return RatingsPresenter(moviesRepository)
    }

    @Provides
    fun provideMovieDetailsPresenter(movieDetailsRepository: MovieDetailsRepository): MovieDetailsContract.Presenter {
        return MovieDetailsPresenter(movieDetailsRepository)
    }
}