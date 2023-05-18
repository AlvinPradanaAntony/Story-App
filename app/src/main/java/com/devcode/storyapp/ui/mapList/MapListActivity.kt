package com.devcode.storyapp.ui.mapList

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityMapListBinding
import com.devcode.storyapp.databinding.BottomSheetBinding
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.home.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class MapListActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapListBinding
    private lateinit var binding2: BottomSheetBinding
    private lateinit var mMap: GoogleMap
    private lateinit var userToken: String
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupView()
        setupAction()

        setSupportActionBar(binding.toolbarId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Maps"
    }

    private fun setupViewModel() {
        mapViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MapViewModel::class.java]

        mapViewModel.getUser().observe(this) { user ->
            userToken = user.token
        }
    }

    private fun setupView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.fr_map_list) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupAction(){
        binding.styleMapButton.setOnClickListener {
            binding2 = BottomSheetBinding.inflate(layoutInflater)
            val bottomSheetDialog = BottomSheetDialog(this@MapListActivity)
            bottomSheetDialog.setContentView(binding2.root)
            bottomSheetDialog.show()

            binding2.styleNormal.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            binding2.styleSatellite.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            binding2.styleTerrain.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            binding2.styleHybrid.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setPadding(0,150,0,0)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
        getLocationUser()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener {
                try {
                    val myLocation = mMap.myLocation
                    if (myLocation != null) {
                        val myLatLng = LatLng(myLocation.latitude, myLocation.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18f))
                        true
                    } else {
                        Snackbar.make(binding.root, "Lokasi tidak tersedia", Snackbar.LENGTH_LONG).show()
                        false
                    }
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Terjadi kesalahan saat mendapatkan lokasi", Snackbar.LENGTH_LONG).show()
                    false
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            try {
                val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }
            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", exception)
            }
        }
    }


    private val boundsBuilder = LatLngBounds.Builder()
    data class TourismPlace(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )
    private fun getLocationUser() {
        mapViewModel.postLocation(userToken)
        mapViewModel.isLocationUser.observe(this) { data ->
            data?.listStory?.forEach { listStoryItem ->
                val name = listStoryItem.name
                val latLng = LatLng(listStoryItem.lat, listStoryItem.lon)
                val addressName = getAddressName(listStoryItem.lat, listStoryItem.lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(name).snippet(addressName))
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
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapListActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d(TAG, "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}