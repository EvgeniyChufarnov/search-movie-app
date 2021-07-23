package com.example.searchmovieapp.data

import com.example.searchmovieapp.BuildConfig
import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Runnable
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val API_PATH = "https://api.themoviedb.org/3/movie/"
private const val POSTER_PATH = "https://image.tmdb.org/t/p/w500/"
private const val NOW_PLAYING_PATH = "now_playing"
private const val UPCOMING_PATH = "upcoming"
private const val TOP_RATED_PATH = "top_rated"
private const val MOVIE_JSON_ARRAY_KEY = "results"

@Singleton
class TmdbRemoteDataSourceImpl @Inject constructor() : RemoteDataSource {
    private var adapterForMovieEntity: JsonAdapter<List<MovieEntity>>
    private var adapterForMovieDetails: JsonAdapter<MovieDetailsEntity>

    init {
        val moshi: Moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, MovieEntity::class.java)
        adapterForMovieEntity = moshi.adapter(type)
        adapterForMovieDetails = moshi.adapter(MovieDetailsEntity::class.java)
    }

    override suspend fun getNowPlayingMovies(pageNum: Int): List<MovieEntity> {
        val response = getDataAsync(createUri(NOW_PLAYING_PATH, pageNum))
        val moviesJsonArray = parseResponse(response)
        val result: List<MovieEntity> = parseMovieListJson(moviesJsonArray) ?: emptyList()

        result.forEach {
            it.posterPath = "$POSTER_PATH${it.posterPath}"
            it.isUpcoming = false
        }

        return result
    }

    override suspend fun getUpcomingMovies(pageNum: Int): List<MovieEntity> {
        val response = getDataAsync(createUri(UPCOMING_PATH, pageNum))
        val moviesJsonArray = parseResponse(response)
        val result: List<MovieEntity> = parseMovieListJson(moviesJsonArray) ?: emptyList()

        result.forEach {
            it.posterPath = "$POSTER_PATH${it.posterPath}"
            it.isUpcoming = true
        }

        return result
    }

    override suspend fun getFavoritesMovies(pageNum: Int): List<MovieEntity> {
        //todo implement getting favorites
        return emptyList()
    }

    override suspend fun getTopRatedMovies(pageNum: Int): List<MovieEntity> {
        val response = getDataAsync(createUri(TOP_RATED_PATH, pageNum))
        val moviesJsonArray = parseResponse(response)
        val result: List<MovieEntity> = parseMovieListJson(moviesJsonArray) ?: emptyList()

        result.forEach {
            it.posterPath = "$POSTER_PATH${it.posterPath}"
            it.isUpcoming = false
        }

        return result
    }

    override suspend fun getMovieDetailsById(movieId: Int): MovieDetailsEntity {
        val response = getDataAsync(createUri(movieId.toString()))

        val result = parseMovieDetailsJson(response)!!

        result.posterPath.let {
            result.posterPath = "$POSTER_PATH${it}"
        }

        return result
    }

    override suspend fun setMovieAsFavorite(movieId: Int, isFavorite: Boolean) {
        //todo implement setting movie as favorite
    }

    private fun createUri(requestType: String, pageNum: Int? = null): String {
        var uri = "$API_PATH$requestType?api_key=${BuildConfig.MOVIES_API_KEY}"

        pageNum?.let {
            uri = uri.plus("&page=$it")
        }

        return uri
    }

    private fun parseResponse(response: String): String {
        return JSONObject(response).getJSONArray(MOVIE_JSON_ARRAY_KEY).toString()
    }

    private suspend fun getDataAsync(url: String): String {
        return suspendCoroutine { continuation ->
            Thread(Runnable {
                (URL(url).openConnection() as HttpsURLConnection).run {
                    try {
                        continuation.resume(inputStream.bufferedReader().readText())
                    } finally {
                        disconnect()
                    }
                }
            }).start()
        }
    }

    private suspend fun parseMovieListJson(moviesJsonArray: String): List<MovieEntity>? {
        return suspendCoroutine { continuation ->
            Thread(Runnable {
                continuation.resume(adapterForMovieEntity.fromJson(moviesJsonArray))
            }).start()
        }
    }

    private suspend fun parseMovieDetailsJson(response: String): MovieDetailsEntity? {
        return suspendCoroutine { continuation ->
            Thread(Runnable {
                continuation.resume(adapterForMovieDetails.fromJson(response))
            }).start()
        }
    }
}