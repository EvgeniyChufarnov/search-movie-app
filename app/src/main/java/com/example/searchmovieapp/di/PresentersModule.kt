package com.example.searchmovieapp.di

import com.example.searchmovieapp.domain.Interactor
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
    fun provideHomePresenter(interactor: Interactor): HomeContract.Presenter {
        return HomePresenter(interactor)
    }

    @Singleton
    @Provides
    fun provideFavoritesPresenter(interactor: Interactor): FavoritesContract.Presenter {
        return FavoritesPresenter(interactor)
    }

    @Singleton
    @Provides
    fun provideRatingsPresenter(interactor: Interactor): RatingsContract.Presenter {
        return RatingsPresenter(interactor)
    }

    @Provides
    fun provideMovieDetailsPresenter(interactor: Interactor): MovieDetailsContract.Presenter {
        return MovieDetailsPresenter(interactor)
    }
}