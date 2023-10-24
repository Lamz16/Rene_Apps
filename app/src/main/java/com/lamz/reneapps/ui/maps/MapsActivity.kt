package com.lamz.reneapps.ui.maps

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.lamz.reneapps.R
import com.lamz.reneapps.data.ResultState
import com.lamz.reneapps.databinding.ActivityMapsBinding
import com.lamz.reneapps.ui.ViewModelFactory
import com.lamz.reneapps.ui.welcome.WelcomeActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        supportActionBar?.hide()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLocation()
        setMapStyle()
        addManyMarker()
    }

    private val boundsBuilder = LatLngBounds.Builder()
    private fun addManyMarker() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStoriesWithLocation(user.token)
                viewModel.location.observe(this) { maps ->
                    if (maps != null) {
                        when (maps) {
                            is ResultState.Loading -> {

                            }

                            is ResultState.Success -> {
                                maps.data.listStory.forEach { storyMaps ->
                                    val latLng = LatLng(storyMaps.lat, storyMaps.lon)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(storyMaps.name)
                                            .snippet(storyMaps.description)
                                    )
                                    boundsBuilder.include(latLng)
                                }

                                val bounds: LatLngBounds = boundsBuilder.build()
                                mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        resources.displayMetrics.widthPixels,
                                        resources.displayMetrics.heightPixels,
                                        300
                                    )
                                )
                            }

                            is ResultState.Error -> {

                            }
                        }
                    }
                }

            }

        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Style parsing failed", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, "Can't find style. Error: $exception", Toast.LENGTH_SHORT).show()

        }
    }

}