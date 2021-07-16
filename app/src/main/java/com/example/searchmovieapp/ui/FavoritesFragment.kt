package com.example.searchmovieapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.searchmovieapp.adapters.MovieListAdapter
import com.example.searchmovieapp.contracts.FavoritesContract
import com.example.searchmovieapp.databinding.FragmentFavoritesBinding
import com.example.searchmovieapp.entities.MovieEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

        attachView()
        showProgressBar()
        initRecyclerView()
        presenter.getMovies()
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun initRecyclerView() {
        binding.favoriteMoviesRecyclerView.adapter =
            MovieListAdapter(true, this::navigateToMovieDetailFragment)
        binding.favoriteMoviesRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showFavorites(favoriteMovies: List<MovieEntity>) {
        updateAdapterDataSet(
            binding.favoriteMoviesRecyclerView.adapter as MovieListAdapter,
            favoriteMovies
        )
        hideProgressBar()
    }

    private fun updateAdapterDataSet(adapter: MovieListAdapter, data: List<MovieEntity>) {
        adapter.setData(data)
    }

    private fun showProgressBar() {
        binding.loadingLayout.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loadingLayout.isVisible = false
    }

    private fun navigateToMovieDetailFragment(movieId: Int) {
        (requireActivity() as HomeFragment.Contract).navigateToMovieDetailFragment(movieId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        check(activity is Contract) { "Activity must implement Favorites.Contract" }
    }

    interface Contract {
        fun navigateToMovieDetailFragment(movieId: Int)
    }
}