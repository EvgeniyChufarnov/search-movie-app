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
import com.example.searchmovieapp.data.remote.entities.MovieEntity
import com.example.searchmovieapp.databinding.FragmentFavoritesBinding
import com.example.searchmovieapp.ui.common.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val COLUMNS_NUM = 3

@AndroidEntryPoint
class FavoritesFragment : Fragment(), FavoritesContract.View {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

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

        initRecyclerView()
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.favoriteMoviesRecyclerView.adapter =
            MovieListAdapter(
                true,
                presenter::navigateToMovieDetailFragment,
                presenter::changeMovieFavoriteState
            )

        val layoutManager =
            GridLayoutManager(requireContext(), COLUMNS_NUM, GridLayoutManager.VERTICAL, false)

        binding.favoriteMoviesRecyclerView.layoutManager = layoutManager
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showFavorites(favoriteMovies: List<MovieEntity>) {
        (binding.favoriteMoviesRecyclerView.adapter as MovieListAdapter).setData(favoriteMovies)
    }

    override fun restoreRecyclerViewPosition(position: Parcelable) {
        binding.favoriteMoviesRecyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    override fun showProgressBar() {
        binding.loadingProcessBar.isVisible = true
    }

    override fun hideProgressBar() {
        binding.loadingProcessBar.isVisible = false
    }

    override fun showNoFavoritesMessage() {
        binding.nothingToShowTextView.isVisible = true
    }

    override fun hideNoFavoritesMessage() {
        binding.nothingToShowTextView.isVisible = false
    }

    override fun showOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = true
    }

    override fun hideOnLostConnectionMessage() {
        binding.noConnectionMessageLayout.isVisible = false
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun getRecyclerViewState(): Parcelable? =
        binding.favoriteMoviesRecyclerView.layoutManager?.onSaveInstanceState()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement Favorites.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}