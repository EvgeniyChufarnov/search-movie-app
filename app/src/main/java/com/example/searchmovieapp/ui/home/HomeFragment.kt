package com.example.searchmovieapp.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.R
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.databinding.FragmentHomeBinding
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val NUM_OF_ITEMS_FROM_LAST_ONE = 5

@AndroidEntryPoint
class HomeFragment : Fragment(),
    HomeContract.View {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: HomeContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViews()
        presenter.attach(this)
    }

    private fun initRecyclerViews() {
        initRecyclerView(binding.nowPlayingRecyclerView, presenter::loadMoreNowPlaying)
        initRecyclerView(binding.upcomingRecyclerView, presenter::loadMoreUpcoming)
    }

    private fun initRecyclerView(recyclerView: RecyclerView, onLoadMore: () -> Unit) {
        recyclerView.adapter = MovieListAdapter(
            false,
            presenter::navigateToMovieDetailFragment,
            presenter::changeMovieFavoriteState
        )
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (layoutManager.findLastVisibleItemPosition() >=
                    recyclerView.adapter?.itemCount?.minus(NUM_OF_ITEMS_FROM_LAST_ONE)!!
                ) {
                    onLoadMore.invoke()
                }
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showMovies(
        nowPlayingMovies: List<MovieEntity>,
        upcomingMovies: List<MovieEntity>
    ) {
        (binding.nowPlayingRecyclerView.adapter as MovieListAdapter).setData(nowPlayingMovies)
        (binding.upcomingRecyclerView.adapter as MovieListAdapter).setData(upcomingMovies)
    }

    override fun showMoreNowPlaying(nowPlayingMovies: List<MovieEntity>) {
        (binding.nowPlayingRecyclerView.adapter as MovieListAdapter).addData(nowPlayingMovies)
    }

    override fun showMoreUpcoming(upcomingMovies: List<MovieEntity>) {
        (binding.upcomingRecyclerView.adapter as MovieListAdapter).addData(upcomingMovies)
    }

    override fun restoreNowPlayingRecyclerViewPosition(position: Parcelable) {
        binding.nowPlayingRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    override fun restoreUpcomingRecyclerViewPosition(position: Parcelable) {
        binding.upcomingRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun getNowPlayingRecyclerViewState() =
        binding.nowPlayingRecyclerView.layoutManager?.onSaveInstanceState()

    override fun getUpcomingRecyclerViewState() =
        binding.upcomingRecyclerView.layoutManager?.onSaveInstanceState()

    override fun showProgressBar() {
        binding.nowPlayingProgressBar.isVisible = true
        binding.upcomingProgressBar.isVisible = true
    }

    override fun hideProgressBar() {
        binding.nowPlayingProgressBar.isVisible = false
        binding.upcomingProgressBar.isVisible = false
    }

    override fun showOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = true
    }

    override fun hideOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = false
    }

    override fun showConnectionError(message: String?) {
        val errorMessage = message ?: getString(R.string.default_network_error)
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement HomeFragment.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}