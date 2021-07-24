package com.example.searchmovieapp.di

import com.example.searchmovieapp.data.FakeRemoteDataSourceImpl
import com.example.searchmovieapp.data.RemoteDataSource
import com.example.searchmovieapp.data.TmdbRemoteDataSourceImpl
import com.example.searchmovieapp.repositories.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {
    @Qualifier
    annotation class FakeDataSource

    @Qualifier
    annotation class TmdbRemoteDataSource

    @FakeDataSource
    @Singleton
    @Binds
    abstract fun bindFakeRemoteDataSource(impl: FakeRemoteDataSourceImpl): RemoteDataSource

    @TmdbRemoteDataSource
    @Singleton
    @Binds
    abstract fun bindTmdbRemoteDataSource(impl: TmdbRemoteDataSourceImpl): RemoteDataSource
}

@InstallIn(SingletonComponent::class)
@Module
class MovieRepositoryModule {
    @Singleton
    @Provides
    fun provideMovieRepository(
        @DataSourceModule.TmdbRemoteDataSource remoteDataSource: RemoteDataSource
    ): MovieRepository {
        return MovieRepository(remoteDataSource)
    }
}