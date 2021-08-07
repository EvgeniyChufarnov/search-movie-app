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
    private var isLoadingCanceled = false
    private var isNowPlayingCanceled = false
    private var isUpcomingCanceled = false
    private var isLoadingMoreNowPlaying = false
    private var isLoadingMoreUpcoming = false

    private lateinit var scope: CoroutineScope

    override fun attach(view: HomeContract.View) {
        this.view = view
        scope = CoroutineScope(Job() + Dispatchers.Main)
        EventBus.getDefault().register(this)

        if (isFirstLoading) {
            view.showProgressBar()
            getMovies()
        } else {
            getAllCachedMovies()
        }
    }

    override fun detach() {
        view = null
        scope.cancel()
        isLoadingCanceled = false
        isNowPlayingCanceled = false
        isUpcomingCanceled = false
        isLoadingMoreNowPlaying = false
        isLoadingMoreUpcoming = false
        EventBus.getDefault().unregister(this)
    }

    private fun getMovies() {
        scope.launch {
            val nowPlayingResult = requestNowPlayingMovies(requestNowPlayingPageNum)
            val upcomingPlayingResult = requestUpcomingMovies(requestUpcomingPageNum)

            when {
                nowPlayingResult is ResultWrapper.Success && upcomingPlayingResult is ResultWrapper.Success -> {
                    view?.showMovies(
                        nowPlayingResult.value.checkFavoritesState(),
                        upcomingPlayingResult.value.checkFavoritesState()
                    )

                    if (isFirstLoading) {
                        isFirstLoading = false
                    }

                    view?.hideProgressBar()
                }
                nowPlayingResult is ResultWrapper.GenericError -> {
                    view?.showConnectionError(nowPlayingResult.error?.message)
                }
                upcomingPlayingResult is ResultWrapper.GenericError -> {
                    view?.showConnectionError(upcomingPlayingResult.error?.message)
                }
                else -> {
                    view?.showConnectionError(null)
                }
            }

            if (!ConnectionState.isAvailable) {
                view?.showOnLostConnectionMessage()
                isLoadingCanceled = true
            }
        }
    }

    private fun getNowPlayingMovies() {
        scope.launch {
            when (val response = requestNowPlayingMovies(requestNowPlayingPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> view?.showMoreNowPlaying(response.value.checkFavoritesState())
            }

            if (!ConnectionState.isAvailable) {
                view?.showOnLostConnectionMessage()
                isNowPlayingCanceled = true
            }
        }
    }

    private fun getUpcomingMovies() {
        scope.launch {
            when (val response = requestUpcomingMovies(requestUpcomingPageNum)) {
                is ResultWrapper.NetworkError -> view?.showConnectionError(null)
                is ResultWrapper.GenericError -> view?.showConnectionError(response.error?.message)
                is ResultWrapper.Success -> view?.showMoreUpcoming(response.value.checkFavoritesState())
            }

            if (!ConnectionState.isAvailable) {
                view?.showOnLostConnectionMessage()
                isUpcomingCanceled = true
            }
        }
    }

    private suspend fun List<MovieEntity>.checkFavoritesState(): List<MovieEntity> {
        forEach {
            it.isFavorite = favoritesRepository.isMovieFavorite(it.id)
        }
        return this
    }

    private fun getAllCachedMovies() {
        scope.launch {
            view?.showMovies(
                moviesRepository.getAllLocalCachedNowPlayingMovies(Locale.getDefault().language)
                    .checkFavoritesState(),
                moviesRepository.getAllLocalCachedUpcomingMovies(Locale.getDefault().language)
                    .checkFavoritesState()
            )

            savedNowPlayingPosition?.let {
                view?.restoreNowPlayingRecyclerViewPosition(it)
                savedNowPlayingPosition = null
            }

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
        if (!isLoadingMoreNowPlaying) {
            isLoadingMoreNowPlaying = true
            if (requestNowPlayingPageNum + 1 <= moviesRepository.nowPlayingTotalPages) {
                requestNowPlayingPageNum++
                getNowPlayingMovies()
            }
        }
    }

    override fun loadMoreUpcoming() {
        if (!isLoadingMoreUpcoming) {
            isLoadingMoreUpcoming = true
            if (requestUpcomingPageNum + 1 <= moviesRepository.upcomingTotalPages) {
                requestUpcomingPageNum++
                getUpcomingMovies()
            }
        }
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        savedNowPlayingPosition = view?.getNowPlayingRecyclerViewState()
        savedUpcomingPosition = view?.getNowPlayingRecyclerViewState()
        view?.navigateToMovieDetailFragment(movieId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectionStateChangedEvent(event: ConnectionStateEvent) {

        if (ConnectionState.isAvailable) {
            view?.hideOnLostConnectionMessage()

            scope = CoroutineScope(Job() + Dispatchers.Main)

            if (isFirstLoading) {
                view?.showProgressBar()
            }

            if (isLoadingCanceled) {
                isLoadingCanceled = false
                getMovies()
            }

            if (isNowPlayingCanceled) {
                isNowPlayingCanceled = false
                getNowPlayingMovies()
            }

            if (isUpcomingCanceled) {
                isUpcomingCanceled = false
                getUpcomingMovies()
            }
        } else {
            view?.showOnLostConnectionMessage()
            view?.hideProgressBar()
            scope.cancel()
        }
    }
}