package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.repositories.MovieDetailsRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MovieDetailsPresenter(private val movieDetailsRepository: MovieDetailsRepository) :
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
                when (val response = getMovieDetailsFromRepository(movieId)) {
                    is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                    is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                    is ResultWrapper.Success -> view?.showDetails(response.value)
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true
            this.movieId = movieId
        }
    }

    private suspend fun getMovieDetailsFromRepository(movieId: Int) =
        movieDetailsRepository.getMovieDetails(movieId, Locale.getDefault().language)

    override fun changeMovieFavoriteState(movieId: Int) {
        //movieDetailsRepository.changeMovieFavoriteState(movieId)
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