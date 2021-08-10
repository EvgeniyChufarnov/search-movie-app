package com.example.searchmovieapp.data

import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.isFavorite
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeRemoteDataSourceImpl @Inject constructor() : RemoteDataSource {
    private val fakeMovies = mutableListOf<MovieEntity>()
    private val fakeDetails = mutableListOf<MovieDetailsEntity>()

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

        for(id in 0..11) {
            fakeDetails.add(
                MovieDetailsEntity(
                    id, fakeMovies[id].title, null, "la movie",
                    233, 500000, 400000, listOf("comedy", "action"),
                    fakeMovies[id].releaseDate, fakeMovies[id].voteAverage, 1254,
                    "Just like any other movie"
                )
            )
        }
    }

    override suspend fun getNowPlayingMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { !it.isUpcoming }
    }

    override suspend fun getUpcomingMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { it.isUpcoming }
    }

    override suspend fun getFavoritesMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { it.isFavorite() }
    }

    override suspend fun getTopRatedMovies(): List<MovieEntity> {
        imitateDelay()
        return fakeMovies.filter { !it.isUpcoming }.sortedBy { -it.voteAverage }
    }

    override suspend fun getMovieDetailsById(movieId: Int): MovieDetailsEntity {
        imitateDelay()
        return fakeDetails[movieId]
    }

    override suspend fun setMovieAsFavorite(movieId: Int, isFavorite: Boolean) {
        imitateDelay()
    }

    private suspend fun imitateDelay() {
        delay(Random.nextLong(1000))
    }
}