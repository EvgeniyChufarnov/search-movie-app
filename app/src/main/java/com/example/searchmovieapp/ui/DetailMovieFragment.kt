package com.example.searchmovieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.searchmovieapp.R
import com.example.searchmovieapp.contracts.MovieDetailsContract
import com.example.searchmovieapp.databinding.FragmentMovieDetailsBinding
import com.example.searchmovieapp.entities.MovieDetailsEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

private const val MOVIE_ID_EXTRA_KEY = "movie id"
private const val STRINGS_SEPARATOR = ", "

@AndroidEntryPoint
class DetailMovieFragment : Fragment(), MovieDetailsContract.View {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: MovieDetailsContract.Presenter

    private var movieId by Delegates.notNull<Int>()

    companion object Instance {
        fun getInstance(movieId: Int) = DetailMovieFragment().apply {
            arguments = Bundle().apply {
                putInt(MOVIE_ID_EXTRA_KEY, movieId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMovieIdFromArguments()
        attachView()
        showProgressBar()
        requestMovieDetails()
    }

    private fun getMovieIdFromArguments() {
        arguments?.run {
            if (containsKey(MOVIE_ID_EXTRA_KEY)) {
                movieId = getInt(MOVIE_ID_EXTRA_KEY)
            }
        }
    }

    private fun attachView() {
        presenter.attach(this)
    }

    private fun requestMovieDetails() {
        presenter.getMovieDetails(movieId)
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showDetails(movieDetails: MovieDetailsEntity) {
        movieDetails.run {
            binding.mainTitleTextView.text = title
            binding.originalTitleTextView.text = originalTitle
            binding.genresTextView.text = genres.joinToString(STRINGS_SEPARATOR)
            binding.budgetTextView.text = getString(R.string.budget, budget)
            binding.revenueTextView.text = getString(R.string.revenue, revenue)
            binding.releaseDateTextView.text = getString(R.string.release_date, releaseDate)
            binding.descriptionTextView.text = overview
            binding.ratingsTextView.text = getString(
                R.string.ratings_with_vote_count,
                voteAverage, voteCount
            )

            runtime?.let {
                binding.durationTextView.text = getString(R.string.duration, runtime)
            }
        }

        hideProgressBar()
        setPoster(movieDetails.posterPath)
    }

    private fun showProgressBar() {
        binding.loadingLayout.isVisible = true
    }

    private fun hideProgressBar() {
        binding.loadingLayout.isVisible = false
    }

    private fun setPoster(path: String?) {
        if (path == null) {
            binding.posterImageView.setImageResource(R.drawable.default_movie_poster)
        } else {
            //todo set image via glide
        }
    }
}
