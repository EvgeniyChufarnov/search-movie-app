package com.example.searchmovieapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.searchmovieapp.R
import com.example.searchmovieapp.databinding.FragmentMovieDetailsBinding
import com.example.searchmovieapp.data.remote.entities.MovieDetailsEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

private const val MOVIE_ID_EXTRA_KEY = "movie id"
private const val STRINGS_SEPARATOR = ", "

@AndroidEntryPoint
class MovieDetailsFragment : Fragment(), MovieDetailsContract.View {
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter: MovieDetailsContract.Presenter
    private var movieDetails: MovieDetailsEntity? = null

    companion object Instance {
        fun getInstance(movieId: Int) = MovieDetailsFragment().apply {
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

        presenter.attach(this, getMovieId())
        setMakeFavoriteListener()
    }

    private fun getMovieId(): Int {
        var movieId = 0

        arguments?.run {
            if (containsKey(MOVIE_ID_EXTRA_KEY)) {
                movieId = getInt(MOVIE_ID_EXTRA_KEY)
            }
        }

        return movieId
    }

    private fun setMakeFavoriteListener() {
        binding.setFavoriteImageView.setOnClickListener {
            movieDetails?.let {
                changeFavoriteButtonImage(!it.isFavorite)
                presenter.changeMovieFavoriteState(it)
            }
        }
    }

    private fun changeFavoriteButtonImage(isFavorite: Boolean) {
        binding.setFavoriteImageView.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    override fun onDestroyView() {
        _binding = null
        presenter.detach()
        super.onDestroyView()
    }

    override fun showDetails(movieDetails: MovieDetailsEntity) {
        this.movieDetails = movieDetails

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

            binding.budgetTextView.isVisible = budget != 0
            binding.revenueTextView.isVisible = revenue != 0

            runtime?.let {
                binding.durationTextView.text = getString(R.string.duration, runtime)
            }

            changeFavoriteButtonImage(isFavorite)
        }

        binding.starImageView.isVisible = true
        binding.setFavoriteImageView.isVisible = true

        setPoster(movieDetails.posterPath)
    }

    override fun showProgressBar() {
        binding.loadingProcessBar.isVisible = true
    }

    override fun hideProgressBar() {
        binding.loadingProcessBar.isVisible = false
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

    private fun setPoster(path: String?) {
        if (path == null) {
            binding.posterImageView.setImageResource(R.drawable.default_movie_poster)
        } else {
            Glide.with(binding.root.context).load(path).into(binding.posterImageView)
        }
    }
}
