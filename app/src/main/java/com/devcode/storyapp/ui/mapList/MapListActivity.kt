package com.devcode.storyapp.ui.mapList

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivityMapListBinding
import com.devcode.storyapp.databinding.BottomSheetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*

class MapListActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapListBinding
    private lateinit var binding2: BottomSheetBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()

        setSupportActionBar(binding.toolbarId)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Maps"
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
                Log.d("BottomSheetDialog", "style_normal diklik")
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            binding2.styleSatellite.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            binding2.styleTerrain.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            binding2.styleHybrid.setOnClickListener {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
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
/*        setMapStyle()*/
        addManyMarker()
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
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }


    private val boundsBuilder = LatLngBounds.Builder()
    data class TourismPlace(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )
    private fun addManyMarker() {
        val tourismPlace = listOf(
            TourismPlace("Rabbit Town", -6.8668408,107.608081),
            TourismPlace("Alun-Alun Kota Bandung", -6.9218518,107.6025294),
            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409),
        )
        tourismPlace.forEach { tourism ->
            val latLng = LatLng(tourism.latitude, tourism.longitude)
            val addressName = getAddressName(tourism.latitude, tourism.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name).snippet(addressName))
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