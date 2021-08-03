package com.example.searchmovieapp.ui.details

import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.entities.MovieDetailsEntity
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.repositories.FavoritesRepository
import com.example.searchmovieapp.repositories.MovieDetailsRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

private fun MovieDetailsEntity.toMovieEntity(): MovieEntity {
    return MovieEntity(id, title, posterPath, releaseDate, voteAverage).also {
        it.isFavorite = this.isFavorite
    }
}

class MovieDetailsPresenter(
    private val movieDetailsRepository: MovieDetailsRepository,
    private val favoritesRepository: FavoritesRepository
) :
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
                    is ResultWrapper.Success -> view?.showDetails(response.value.checkFavoriteState())
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isLoadingCanceled = true
            this.movieId = movieId
        }
    }

    private suspend fun MovieDetailsEntity.checkFavoriteState(): MovieDetailsEntity {
        isFavorite = favoritesRepository.isMovieFavorite(id)
        return this
    }

    private suspend fun getMovieDetailsFromRepository(movieId: Int) =
        movieDetailsRepository.getMovieDetails(movieId, Locale.getDefault().language)

    override fun changeMovieFavoriteState(movieDetails: MovieDetailsEntity) {
        val movieEntity = movieDetails.toMovieEntity()

        scope.launch {
            if (movieEntity.isFavorite) {
                movieDetails.isFavorite = false
                favoritesRepository.removeFromFavorites(movieEntity)
            } else {
                movieDetails.isFavorite = true
                favoritesRepository.addToFavorites(movieEntity)
            }
        }
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