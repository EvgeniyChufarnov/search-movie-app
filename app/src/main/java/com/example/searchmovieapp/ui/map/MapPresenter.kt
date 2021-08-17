package com.example.searchmovieapp.ui.map

import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

private const val LOCATION_UPDATE_INTERVAL = 6000L

class MapPresenter : MapContract.Presenter {
    private var view: MapContract.View? = null
    private var currentLocation: Location? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var isSubscribed = false

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.let {
                currentLocation = it.lastLocation
                view?.addMarkerToMap(it.lastLocation)

                if (isSubscribed) {
                    stopLocationUpdates()
                }
            }
        }
    }

    override fun attach(view: MapContract.View) {
        this.view = view

        currentLocation?.let {
            view.addMarkerToMap(it)
        }

        if (currentLocation == null) {
            fusedLocationClient = view.getFusedLocationClient()
            view.checkPermissions()
        }
    }

    override fun detach() {
        view = null
        fusedLocationClient = null
    }

    override fun onPermissionsGranted() {
        createLocationRequest()
    }

    override fun onShouldShowRequestPermissionRationale() {
        view?.showRationaleDialog()
    }

    override fun onNoPermissions() {
        view?.requestPermissions()
    }

    override fun onAgreeToRequestPermissions() {
        view?.requestPermissions()
    }

    override fun onPermissionsDenied() {
        view?.showOnRefuseDialog()
    }

    override fun onResume() {
        if (isSubscribed) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        if (isSubscribed) {
            stopLocationUpdates()
        }
    }

    private fun createLocationRequest() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient? = view?.getSettingsClient()

        val task: Task<LocationSettingsResponse>? = client?.checkLocationSettings(builder.build())

        task?.addOnSuccessListener { locationSettingsResponse ->
            locationSettingsResponse.locationSettingsStates?.let {
                if (it.isLocationPresent) {
                    getLastLocation()
                } else {
                    view?.showOnRefuseDialog()
                }
            }
        }

        task?.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    view?.startResolutionForResult(exception)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                    view?.addMarkerToMap(it)
                }

                if (location == null) {
                    startLocationUpdates()
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        isSubscribed = true
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        isSubscribed = false
    }
}