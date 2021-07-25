package com.example.searchmovieapp.ui.favorites

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.databinding.FragmentFavoritesBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val COLUMNS_NUM = 3
private const val NUM_OF_ITEMS_FROM_LAST_ONE = 5

@AndroidEntryPoint
class FavoritesFragment : Fragment(), FavoritesContract.View {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private var isLoadingMore = false

    @Inject
    lateinit var presenter: FavoritesContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        attachView()
        initRecyclerView()

        if (presenter.isFirstLoading()) {
            showProgressBar()
        }

        presenter.getMovies()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.favoriteMoviesRecyclerView.adapter =
            MovieListAdapter(
                true,
                this::navigateToMovieDetailFragment,
                this::changeMovieFavoriteState
            )

        val layoutManager =
            GridLayoutManager(requireContext(), COLUMNS_NUM, GridLayoutManager.VERTICAL, false)

        binding.favoriteMoviesRecyclerView.layoutManager = layoutManager

        binding.favoriteMoviesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (layoutManager.findLastVisibleItemPosition() >=
                    recyclerView.adapter?.itemCount?.minus(NUM_OF_ITEMS_FROM_LAST_ONE)!!
                ) {
                    loadMore()
                }
            }
        })
    }

    private fun loadMore() {
        if (!isLoadingMore) {
            isLoadingMore = true
            presenter.loadMore()
        }
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showFavorites(favoriteMovies: List<MovieEntity>) {
        isLoadingMore = false

        updateAdapterDataSet(
            binding.favoriteMoviesRecyclerView.adapter as MovieListAdapter,
            favoriteMovies
        )

        if (presenter.isFirstLoading()) {
            hideProgressBar()
            presenter.firstLoadingDone()
        }

        if (favoriteMovies.isEmpty()) {
            showNoFavoritesMessage()
        } else {
            hideNoFavoritesMessage()
        }
    }

    override fun restoreRecyclerViewPosition(position: Parcelable) {
        binding.favoriteMoviesRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
    }

    private fun showProgressBar() {
        binding.loadingProcessBar.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loadingProcessBar.isVisible = false
    }

    private fun showNoFavoritesMessage() {
        binding.nothingToShowTextView.isVisible = true
    }

    private fun hideNoFavoritesMessage() {
        binding.nothingToShowTextView.isVisible = false
    }

    override fun showOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = true
        hideProgressBar()
    }

    override fun hideOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = false

        if (presenter.isFirstLoading()) {
            showProgressBar()
        }
    }

    private fun navigateToMovieDetailFragment(movieId: Int) {
        saveRecyclerViewState()
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    private fun saveRecyclerViewState() {
        val recyclerViewState =
            binding.favoriteMoviesRecyclerView.layoutManager?.onSaveInstanceState()
        recyclerViewState?.let {
            presenter.saveRecyclerViewPosition(it)
        }
    }

    private fun changeMovieFavoriteState(movieId: Int) {
        presenter.changeMovieFavoriteState(movieId)
        presenter.getMovies()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement Favorites.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}