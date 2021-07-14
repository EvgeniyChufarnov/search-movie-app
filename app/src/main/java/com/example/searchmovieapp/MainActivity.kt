package com.example.searchmovieapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.Contract {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFullScreenContainer()
    }

    private fun initFullScreenContainer() {
        supportFragmentManager.beginTransaction()
            .add(R.id.full_screen_container, NavigationContainerFragment())
            .commit()
    }

    override fun navigateToMovieDetailFragment(movieId: Int) {
        val detailMovieFragment = DetailMovieFragment.getInstance(movieId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.full_screen_container, detailMovieFragment)
            .addToBackStack(null)
            .commit()
    }
}