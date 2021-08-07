package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.domain.ConnectionState
import com.example.searchmovieapp.domain.ConnectionStateEvent
import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.Interactor
import com.example.searchmovieapp.domain.data.ResultWrapper
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private fun MovieDetailsEntity.toMovieEntity(): MovieEntity {
    return MovieEntity(id, title, posterPath, releaseDate, voteAverage).also {
        it.isFavorite = this.isFavorite
    }
}

class MovieDetailsPresenter(
    private val interactor: Interactor
) :
    MovieDetailsContract.Presenter {

    private var view: MovieDetailsContract.View? = null
    private var scope = CoroutineScope(Job() + Dispatchers.Main)
    private var isLoadingCanceled = false
    private var movieId = 0
    private var isLoaded: Boolean = false

    override fun attach(view: MovieDetailsContract.View, movieId: Int) {
        this.view = view
        EventBus.getDefault().register(this)
        this.movieId = movieId
        view.showProgressBar()
        getMovieDetails()
    }

    override fun detach() {
        view = null
        scope.cancel()
        isLoadingCanceled = false
        movieId = 0
        isLoaded = false
        EventBus.getDefault().unregister(this)
    }

    private fun getMovieDetails() {
        if (ConnectionState.isAvailable) {
            scope.launch {
                when (val response = getMovieDetailsFromRepository(movieId)) {
                    is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                    is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                    is ResultWrapper.Success -> {
                        view?.showDetails(response.value)
                        view?.hideProgressBar()
                        isLoaded = true
                    }
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
            isLoadingCanceled = true
        }
    }

    private suspend fun getMovieDetailsFromRepository(movieId: Int) =
        interactor.getMovieDetails(movieId)

    override fun changeMovieFavoriteState(movieDetails: MovieDetailsEntity) {
        val movieEntity = movieDetails.toMovieEntity()

        scope.launch {
            if (movieEntity.isFavorite) {
                movieDetails.isFavorite = false
                interactor.removeFromFavorites(movieEntity)
            } else {
                movieDetails.isFavorite = true
                interactor.addToFavorites(movieEntity)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {

        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            if (!isLoaded) {
                view?.showProgressBar()
            }

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isLoadingCanceled) {
                isLoadingCanceled = false
                getMovieDetails()
            }
        } else {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
            scope.cancel()
        }
    }
}