package com.example.searchmovieapp.ui.home

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.data.ResultWrapper
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.domain.repositories.FavoritesRepository
import com.example.searchmovieapp.domain.repositories.MoviesRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class HomePresenter(
    private val moviesRepository: MoviesRepository,
    private val favoritesRepository: FavoritesRepository
) :
    HomeContract.Presenter {

    private var view: HomeContract.View? = null
    private var requestNowPlayingPageNum = 1
    private var requestUpcomingPageNum = 1
    private var savedNowPlayingPosition: Parcelable? = null
    private var savedUpcomingPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isGetNowPlayingCanceled = false
    private var isGetUpcomingCanceled = false

    private lateinit var scope: CoroutineScope

    override fun attach(view: HomeContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
        EventBus.getDefault().register(this)
    }

    override fun firstLoadingDone() {
        isFirstLoading = false
    }

    override fun detach() {
        view = null
        scope.cancel()
        isGetNowPlayingCanceled = false
        isGetUpcomingCanceled = false
        EventBus.getDefault().unregister(this)
    }

    override fun isFirstLoading() = isFirstLoading

    override fun getMovies() {
        getNowPlayingMovies()
        getUpcomingMovies()
    }

    private fun getNowPlayingMovies() {
        scope.launch {
            when (val response = requestNowPlayingMovies(requestNowPlayingPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> view?.showNowPlaying(response.value.checkFavoritesState())
            }
        }

        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
            isGetNowPlayingCanceled = true
        }
    }

    private fun getUpcomingMovies() {
        scope.launch {
            when (val response = requestUpcomingMovies(requestUpcomingPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> view?.showUpcoming(response.value.checkFavoritesState())
            }
        }

        if (!ConnectionState.isAvailable) {
            view?.showOnLostConnectionMessage()
            isGetUpcomingCanceled = true
        }
    }

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach {
            it.isFavorite = favoritesRepository.isMovieFavorite(it.id)
        }
        return this
    }

    override fun getAllCachedMovies() {
        scope.launch {
            view?.showNowPlaying(
                moviesRepository.getAllLocalCachedNowPlayingMovies(Locale.getDefault().language)
                    .checkFavoritesState()
            )

            savedNowPlayingPosition?.let {
                view?.restoreNowPlayingRecyclerViewPosition(it)
                savedNowPlayingPosition = null
            }
        }

        scope.launch {
            view?.showUpcoming(
                moviesRepository.getAllLocalCachedUpcomingMovies(Locale.getDefault().language)
                    .checkFavoritesState()
            )

            savedUpcomingPosition?.let {
                view?.restoreUpcomingRecyclerViewPosition(it)
                savedUpcomingPosition = null
            }
        }
    }

    private suspend fun requestNowPlayingMovies(pageNum: Int) =
        moviesRepository.getNowPlayingMovies(pageNum, Locale.getDefault().language)

    private suspend fun requestUpcomingMovies(pageNum: Int) =
        moviesRepository.getUpcomingMovies(pageNum, Locale.getDefault().language)

    override fun changeMovieFavoriteState(movie: MovieEntity) {
        scope.launch {
            if (movie.isFavorite) {
                favoritesRepository.removeFromFavorites(movie)
            } else {
                favoritesRepository.addToFavorites(movie)
            }

            getAllCachedMovies()
        }
    }

    override fun loadMoreNowPlaying() {
        if (requestNowPlayingPageNum + 1 <= moviesRepository.nowPlayingTotalPages) {
            requestNowPlayingPageNum++
            getNowPlayingMovies()
        }
    }

    override fun loadMoreUpcoming() {
        if (requestUpcomingPageNum + 1 <= moviesRepository.upcomingTotalPages) {
            requestUpcomingPageNum++
            getUpcomingMovies()
        }
    }

    override fun saveNowPlayingRecyclerViewPosition(position: Parcelable) {
        savedNowPlayingPosition = position
    }

    override fun saveUpcomingRecyclerRecyclerViewPosition(position: Parcelable) {
        savedUpcomingPosition = position
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {

        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isGetNowPlayingCanceled) {
                isGetNowPlayingCanceled = false
                getNowPlayingMovies()
            }

            if (isGetUpcomingCanceled) {
                isGetUpcomingCanceled = false
                getUpcomingMovies()
            }
        } else {
            view?.showOnLostConnectionMessage()
            scope.cancel()
        }
    }
}