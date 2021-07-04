package com.example.searchmovieapp.injection

import android.app.Application

class MovieApplication : Application() {
    val appContainer = AppContainer()
}