package com.example.searchmovieapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.searchmovieapp.ConnectionState
import com.example.searchmovieapp.R
import com.example.searchmovieapp.ui.details.MovieDetailsFragment
import com.example.searchmovieapp.ui.favorites.FavoritesFragment
import com.example.searchmovieapp.ui.home.HomeFragment
import com.example.searchmovieapp.ui.ratings.RatingsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.Contract, FavoritesFragment.Contract,
    RatingsFragment.Contract {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFullScreenContainer()
        ConnectionState.setContext(this)
    }

    private fun initFullScreenContainer() {
        supportFragmentManager.beginTransaction()
            .add(R.id.full_screen_container, NavigationContainerFragment())
            .commit()
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        val detailMovieFragment = MovieDetailsFragment.getInstance(movieId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.full_screen_container, detailMovieFragment)
            .addToBackStack(null)
            .commit()
    }
}