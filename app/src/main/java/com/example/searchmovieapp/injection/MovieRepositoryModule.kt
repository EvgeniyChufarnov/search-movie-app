package com.example.searchmovieapp.injection

import com.example.searchmovieapp.data.FakeRemoteDataSourceImpl
import com.example.searchmovieapp.data.RemoteDataSource
import com.example.searchmovieapp.repositories.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class FakeDatabaseModule {

    @Singleton
    @Binds
    abstract fun bindFakeRemoteDataSource(impl: FakeRemoteDataSourceImpl): RemoteDataSource
}

@InstallIn(SingletonComponent::class)
@Module
class MovieRepositoryModule {

    @Provides
    fun provideMovieRepository(remoteDataSource: RemoteDataSource): MovieRepository {
        return MovieRepository(remoteDataSource)
    }
}