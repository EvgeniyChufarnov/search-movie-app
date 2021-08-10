package com.example.searchmovieapp.di

import com.example.searchmovieapp.contracts.FavoritesContract
import com.example.searchmovieapp.contracts.HomeContract
import com.example.searchmovieapp.contracts.MovieDetailsContract
import com.example.searchmovieapp.contracts.RatingsContract
import com.example.searchmovieapp.presenters.FavoritesPresenter
import com.example.searchmovieapp.presenters.HomePresenter
import com.example.searchmovieapp.presenters.MovieDetailsPresenter
import com.example.searchmovieapp.presenters.RatingsPresenter
import com.example.searchmovieapp.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
class HomePresenterModule {
    @Provides
    fun provideHomePresenter(movieRepository: MovieRepository): HomeContract.Presenter {
        return HomePresenter(movieRepository)
    }
}

@InstallIn(FragmentComponent::class)
@Module
class MovieDetailsPresenterModule {
    @Provides
    fun provideMovieDetailsPresenter(movieRepository: MovieRepository): MovieDetailsContract.Presenter {
        return MovieDetailsPresenter(movieRepository)
    }
}

@InstallIn(FragmentComponent::class)
@Module
class FavoritesPresenterModule {
    @Provides
    fun provideFavoritesPresenter(movieRepository: MovieRepository): FavoritesContract.Presenter {
        return FavoritesPresenter(movieRepository)
    }
}

@InstallIn(FragmentComponent::class)
@Module
class RatingsPresenterModule {
    @Provides
    fun provideRatingsPresenter(movieRepository: MovieRepository): RatingsContract.Presenter {
        return RatingsPresenter(movieRepository)
    }
}