package com.example.searchmovieapp.ui.map

import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.SettingsClient

interface MapContract {
    interface View {
        fun addMarkerToMap(location: Location)
        fun checkPermissions()
        fun requestPermissions()
        fun showRationaleDialog()
        fun showOnRefuseDialog()
        fun getSettingsClient(): SettingsClient
        fun startResolutionForResult(exception: ResolvableApiException)
        fun getFusedLocationClient(): FusedLocationProviderClient
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onPermissionsGranted()
        fun onShouldShowRequestPermissionRationale()
        fun onNoPermissions()
        fun onAgreeToRequestPermissions()
        fun onPermissionsDenied()
        fun onLocationServicesTurnedOn()
        fun onPause()
        fun onResume()
    }
}