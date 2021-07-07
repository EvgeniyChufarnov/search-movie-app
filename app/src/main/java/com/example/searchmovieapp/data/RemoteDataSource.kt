package com.example.searchmovieapp.data

import com.example.searchmovieapp.entities.MovieEntity
import kotlinx.coroutines.delay
import kotlin.random.Random

interface RemoteDataSource {
    suspend fun getNowPlayingMovies(): List<MovieEntity>
    suspend fun getUpcomingMovies(): List<MovieEntity>
}

class FakeRemoteDataSourceImpl: RemoteDataSource {
    private val fakeMovies = mutableListOf<MovieEntity>()

    init {
        fakeMovies.add(MovieEntity(0, "First", null, "2020-05-28", 4.6f, false))
        fakeMovies.add(MovieEntity(1, "Second", null, "2021-05-28", 4.7f, false))
        fakeMovies.add(MovieEntity(2, "Another", null, "1999-05-28", 10f, true))
        fakeMovies.add(MovieEntity(3, "Some Movie", null, "2002-05-28", 2f, false))
        fakeMovies.add(MovieEntity(4, "Good Movie", null, "2007-05-28", 4.6f, false))
        fakeMovies.add(MovieEntity(5, "Average Movie", null, "2000-05-28", 0.6f, true))
        fakeMovies.add(MovieEntity(6, "Bad Movie", null, "1958-05-28", 5.6f, false))
        fakeMovies.add(MovieEntity(7, "Really Bad Movie", null, "1999-05-28", 4.7f, true))
        fakeMovies.add(MovieEntity(8, "Russian Movie", null, "1997-05-28", 4.8f, false))
        fakeMovies.add(MovieEntity(9, "Movie with a really long title, part two", null, "1974-05-28", 3.6f, true))
        fakeMovies.add(MovieEntity(10, "Popular movie", null, "1998-05-28", 5f, false))
        fakeMovies.add(MovieEntity(11, "A movie no one cares about", null, "1999-05-28", 4.0f, true))
    }

    override suspend fun getNowPlayingMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { !it.isUpcoming }
    }

    override suspend fun getUpcomingMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { it.isUpcoming }
    }

    private suspend fun imitateDelay() {
        delay(Random.nextLong(5000))
    }
}