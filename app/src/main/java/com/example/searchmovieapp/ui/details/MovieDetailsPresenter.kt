package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MovieDetailsPresenter(private val movieRepository: MovieRepository) :
    MovieDetailsContract.Presenter {

    private var view: MovieDetailsContract.View? = null
    private var scope = CoroutineScope(Job() + Dispatchers.Main)
    private var isLoadingCanceled = false
    private var movieId = 0

    override fun attach(view: MovieDetailsContract.View) {
        this.view = view
        EventBus.getDefault().register(this)
    }

    override fun detach() {
        view = null
        scope.cancel()
        isLoadingCanceled = false
        movieId = 0
        EventBus.getDefault().unregister(this)
    }

    override fun getMovieDetails(movieId: Int) {
        if (ConnectionState.isAvailable) {
            scope.launch {
                view?.showDetails(getMovieDetailsFromRepository(movieId))
            }
        } else {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true
            this.movieId = movieId
        }
    }

    private suspend fun getMovieDetailsFromRepository(movieId: Int) = withContext(Dispatchers.IO) {
        movieRepository.getMovieDetailsById(movieId)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {

        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isLoadingCanceled) {
                isLoadingCanceled = false
                getMovieDetails(movieId)
            }
        } else {
            view?.showOnLostConnectionMessage()
            scope.cancel()
        }
    }
}