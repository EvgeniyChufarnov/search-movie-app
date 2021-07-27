package com.example.searchmovieapp.ui.home

import android.os.Parcelable
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.ConnectionStateEvent
import com.example.searchmovieapp.repositories.MovieRepository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomePresenter(private val movieRepository: MovieRepository) :
    HomeContract.Presenter {

    private var view: HomeContract.View? = null
    private var requestNowPlayingPageNum = 1
    private var requestUpcomingPageNum = 1
    private var savedNowPlayingPosition: Parcelable? = null
    private var savedUpcomingPosition: Parcelable? = null
    private var isFirstLoading = true
    private var isGetNowPlayingCanceled = false
    private var isGetUpcomingCanceled = false
    private var isCacheLoaded = false

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
        isCacheLoaded = false
        EventBus.getDefault().unregister(this)
    }

    override fun isFirstLoading() = isFirstLoading

    override fun getMovies() {
        getNowPlayingMovies()
        getUpcomingMovies()
    }

    private fun getNowPlayingMovies() {
        if (ConnectionState.isAvailable) {
            scope.launch {
                view?.showNowPlaying(requestNowPlayingMovies(requestNowPlayingPageNum))

                savedNowPlayingPosition?.let {
                    view?.restoreNowPlayingRecyclerViewPosition(it)
                    savedNowPlayingPosition = null
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isGetNowPlayingCanceled = true

            if (!isCacheLoaded) {
                isCacheLoaded = true
                loadCache()
            }
        }
    }

    private fun getUpcomingMovies() {
        if (ConnectionState.isAvailable) {
            scope.launch {
                view?.showUpcoming(requestUpcomingMovies(requestUpcomingPageNum))

                savedUpcomingPosition?.let {
                    view?.restoreUpcomingRecyclerViewPosition(it)
                    savedUpcomingPosition = null
                }
            }
        } else {
            view?.showOnLostConnectionMessage()
            isGetUpcomingCanceled = true
        }
    }

    private suspend fun requestNowPlayingMovies(pageNum: Int) = withContext(Dispatchers.IO) {
        movieRepository.getNowPlayingMovies(pageNum)
    }

    private suspend fun requestUpcomingMovies(pageNum: Int) = withContext(Dispatchers.IO) {
        movieRepository.getUpcomingMovies(pageNum)
    }

    override fun changeMovieFavoriteState(movieId: Int) {
        movieRepository.changeMovieFavoriteState(movieId)
    }

    private fun loadCache() {
        if (!isFirstLoading) {
            scope.launch {
                view?.showNowPlaying(
                    requestNowPlayingMovies(
                        if (requestNowPlayingPageNum == 1) 1
                        else requestNowPlayingPageNum - 1
                    )
                )

                savedNowPlayingPosition?.let {
                    view?.restoreNowPlayingRecyclerViewPosition(it)
                    savedNowPlayingPosition = null
                }
            }

            scope.launch {
                view?.showUpcoming(
                    requestUpcomingMovies(
                        if (requestUpcomingPageNum == 1) 1
                        else requestUpcomingPageNum - 1
                    )
                )

                savedUpcomingPosition?.let {
                    view?.restoreUpcomingRecyclerViewPosition(it)
                    savedUpcomingPosition = null
                }
            }
        }
    }

    override fun loadMoreNowPlaying() {
        requestNowPlayingPageNum++
        getNowPlayingMovies()
    }

    override fun loadMoreUpcoming() {
        requestUpcomingPageNum++
        getUpcomingMovies()
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