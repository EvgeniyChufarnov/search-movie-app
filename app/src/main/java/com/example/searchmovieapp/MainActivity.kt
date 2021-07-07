package com.example.searchmovieapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
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
}