package com.example.searchmovieapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.searchmovieapp.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val ZOOM = 15f

@AndroidEntryPoint
class MapFragment : Fragment(), MapContract.View {

    private lateinit var map: GoogleMap

    @Inject
    lateinit var presenter: MapContract.Presenter

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isPermissionGranted = true

            permissions.entries.forEach { (_, isGranted) ->
                if (!isGranted) isPermissionGranted = false
            }

            if (isPermissionGranted) {
                presenter.onPermissionsGranted()
            } else {
                presenter.onPermissionsDenied()
            }
        }

    private val launcherForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                presenter.onLocationServicesTurnedOn()
            } else {
                presenter.onPermissionsDenied()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
        }

        presenter.attach(this)
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun getFusedLocationClient(): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                presenter.onPermissionsGranted()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                presenter.onShouldShowRequestPermissionRationale()
            }
            else -> {
                presenter.onNoPermissions()
            }
        }
    }

    override fun requestPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_rationale_title))
            .setMessage(getString(R.string.dialog_rationale_message))
            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                presenter.onAgreeToRequestPermissions()
            }
            .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun showOnRefuseDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_on_refuse_title))
            .setMessage(getString(R.string.dialog_on_refuse_message))
            .setNegativeButton(getString(R.string.dialog_on_refuse_decline)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun getSettingsClient(): SettingsClient {
        return LocationServices.getSettingsClient(requireContext())
    }

    override fun startResolutionForResult(exception: ResolvableApiException) {
        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
        launcherForResult.launch(intentSenderRequest)
    }

    override fun addMarkerToMap(location: Location) {
        map.clear()
        map.addMarker(
            MarkerOptions().position(LatLng(location.latitude, location.longitude))
                .title(getString(R.string.your_location))
        )
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), ZOOM
            )
        )
    }
}