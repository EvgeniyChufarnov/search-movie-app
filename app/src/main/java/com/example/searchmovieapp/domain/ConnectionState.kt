package com.example.searchmovieapp.domain

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import org.greenrobot.eventbus.EventBus

class ConnectionStateEvent

object ConnectionState {
    private lateinit var connectivityManager: ConnectivityManager
    private val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

    var isAvailable: Boolean = false

    fun setContext(context: Context) {
        connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        registerCallbacks()
    }

    private fun registerCallbacks() {
        connectivityManager.registerNetworkCallback(networkRequestBuilder.build(), object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isAvailable = true
                EventBus.getDefault().post(ConnectionStateEvent())
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isAvailable = false
                EventBus.getDefault().post(ConnectionStateEvent())
            }
        })
    }
}