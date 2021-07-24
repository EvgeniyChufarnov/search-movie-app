package com.example.searchmovieapp.di

import com.example.searchmovieapp.repositories.MovieRepository
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
    fun provideHomePresenter(movieRepository: MovieRepository): HomeContract.Presenter {
        return HomePresenter(movieRepository)
    }

    @Singleton
    @Provides
    fun provideFavoritesPresenter(movieRepository: MovieRepository): FavoritesContract.Presenter {
        return FavoritesPresenter(movieRepository)
    }

    @Singleton
    @Provides
    fun provideRatingsPresenter(movieRepository: MovieRepository): RatingsContract.Presenter {
        return RatingsPresenter(movieRepository)
    }

    @Provides
    fun provideMovieDetailsPresenter(movieRepository: MovieRepository): MovieDetailsContract.Presenter {
        return MovieDetailsPresenter(movieRepository)
    }
}