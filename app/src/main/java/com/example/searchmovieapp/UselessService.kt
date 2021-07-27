package com.example.searchmovieapp

import android.app.Service
import android.content.Intent
import android.util.Log
import com.example.searchmovieapp.repositories.MovieRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val PAGE_NUM = 1

@AndroidEntryPoint
class UselessService : Service() {

    @Inject
    lateinit var movieRepository: MovieRepository

    private val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ConnectionState.isAvailable) {
            scope.launch {
                val movies = movieRepository.getTopRatedMovies(PAGE_NUM)
                Log.i(this@UselessService.javaClass.name, movies.toString())
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): Nothing? = null
}