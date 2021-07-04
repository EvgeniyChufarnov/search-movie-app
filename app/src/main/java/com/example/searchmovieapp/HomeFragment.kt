package com.example.searchmovieapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.searchmovieapp.adapters.MovieListAdapter
import com.example.searchmovieapp.databinding.FragmentHomeBinding
import com.example.searchmovieapp.entities.MovieEntity
import com.example.searchmovieapp.injection.MovieApplication
import com.example.searchmovieapp.presenters.HomePresenter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: HomePresenter

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

        createPresenter()

        initRecyclerView(binding.rvNowPlaying, presenter.getNowPlayingMovies())
        initRecyclerView(binding.rvUpcoming, presenter.getUpcomingMovies())
    }

    private fun createPresenter() {
        val appContainer = (requireActivity().application as MovieApplication).appContainer
        presenter = appContainer.homePresenterFactory.create()
    }

    private fun initRecyclerView(recyclerView: RecyclerView, movies: List<MovieEntity>) {
        recyclerView.adapter = MovieListAdapter(movies)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}