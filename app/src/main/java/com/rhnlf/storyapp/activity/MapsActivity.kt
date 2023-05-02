package com.rhnlf.storyapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.data.remote.response.ListStoryItem
import com.rhnlf.storyapp.databinding.ActivityMapsBinding
import com.rhnlf.storyapp.helper.ScreenState
import com.rhnlf.storyapp.view.MapsViewModel
import com.rhnlf.storyapp.view.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    private var boundsBuilder = LatLngBounds.Builder()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) getMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        initViewModel()

        title = getString(R.string.maps)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.unable_to_get_permission), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupView() {
        supportActionBar?.title = getString(R.string.app_name)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[MapsViewModel::class.java]

        viewModel.getStory().observe(this) {
            determineState(it)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = false
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapsStyle()
    }

    private fun determineState(state: ScreenState<List<ListStoryItem>>?) {
        when (state) {
            is ScreenState.Loading -> {
                showErrorLayout(false)
            }

            is ScreenState.Success -> {
                showErrorLayout(false)
                if (state.data != null) {
                    val storyList = state.data
                    storyList.forEach {
                        val latitude = it.lat as Double
                        val longitude = it.lon as Double
                        val latLng = LatLng(latitude, longitude)
                        val title = it.name as String
                        val desc = it.description as String
                        mMap.addMarker(
                            MarkerOptions().position(latLng).title(title).snippet(desc)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        boundsBuilder.include(latLng)
                    }

                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            200
                        )
                    )
                }
            }

            is ScreenState.Error -> {
                Log.e(TAG, "onError: ${state.message}")
                showErrorLayout(true)
            }

            else -> {}
        }
    }

    private fun setMapsStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) Log.e(TAG, "Style parsing failed")
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showErrorLayout(isError: Boolean) {
        binding.errorLayout.visibility = if (isError) View.VISIBLE else View.GONE
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "MapsActivity"
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSION = 20
    }
}