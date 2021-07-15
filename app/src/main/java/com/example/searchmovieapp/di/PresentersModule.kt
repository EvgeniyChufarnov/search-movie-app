package com.example.searchmovieapp.di

import com.example.searchmovieapp.contracts.HomeScreenContract
import com.example.searchmovieapp.contracts.MovieDetailsContract
import com.example.searchmovieapp.presenters.HomePresenter
import com.example.searchmovieapp.presenters.MovieDetailsPresenter
import com.example.searchmovieapp.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
class HomePresenterModule {
    @Provides
    fun provideHomePresenter(movieRepository: MovieRepository): HomeScreenContract.Presenter {
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