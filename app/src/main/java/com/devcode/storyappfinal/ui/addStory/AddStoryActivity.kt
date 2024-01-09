package com.devcode.storyappfinal.ui.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyappfinal.*
import com.devcode.storyappfinal.databinding.ActivityAddStoryBinding
import com.devcode.storyappfinal.ui.cameraActivity.CameraActivity
import com.devcode.storyappfinal.ui.main.MainActivity
import com.devcode.storyappfinal.utils.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.*


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var userToken: String
    private lateinit var factory: ViewModelFactory
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var isCheckSwitchGuest: Boolean = false
    private var isCheckSwitchLoc: Boolean = false
    private var getFile: File? = null
    private var location: Location? = null
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.no_permission,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        switchUser(isCheckSwitchGuest, isCheckSwitchLoc)
        setupAction()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
        addStoryViewModel.getUser().observe(this) {
            userToken = it.token
        }
    }

    private fun switchUser(isCheckSwitchGuest: Boolean, isCheckSwitchLoc: Boolean) {
        binding.apply {
            switchUser.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                this@AddStoryActivity.isCheckSwitchGuest = isChecked != isCheckSwitchGuest
                if (this@AddStoryActivity.isCheckSwitchGuest){
                    Toast.makeText(this@AddStoryActivity, "Now, you as guest", Toast.LENGTH_SHORT).show()
                }
            }
            switchLocation.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                this@AddStoryActivity.isCheckSwitchLoc = isChecked != isCheckSwitchLoc
                if (this@AddStoryActivity.isCheckSwitchLoc){
                    Log.d("TAG", "switchUser: $isCheckSwitchLoc")
                    getMyLocation()
                }
            }
        }
    }

    private fun setupAction(){
        binding.buttonCamera.setOnClickListener { startCameraX() }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text?.trim().toString()
            if (description.isEmpty() && getFile == null) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage(R.string.not_empty)
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            } else if (description.isEmpty()){
                binding.edAddDescription.error = resources.getString(R.string.must_filled)
                binding.edAddDescription.requestFocus()
            } else {
                uploadImage()
            }
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation = LocationServices.getFusedLocationProviderClient(this)
            fusedLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    val addressName = getAddressName(lat!!, lon!!)
                    Snackbar.make(
                        binding.root,
                        addressName.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Failed to get location",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            (Manifest.permission.ACCESS_COARSE_LOCATION)

        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImg.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.previewImg.setImageURI(uri)
            }
        }
    }

    private fun uploadImage() {
        showLoading(true)
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val token = "Bearer $userToken"
            val isAddStory = if (isCheckSwitchGuest) {
                addStoryViewModel.addStoryAsGuest(imageMultipart, description, lat, lon)
            } else {
                addStoryViewModel.addStory(token, imageMultipart, description, lat, lon)
            }
            isAddStory.observe(this) {
                when (it) {
                    is Result.Success -> {
                        showLoading(false)
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, R.string.upload_success, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "${R.string.upload_failed}: "+it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(this@AddStoryActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
                Log.d("AddressName", "getAddressName: $addressName")
            }
        } catch (e: IOException) {
            Log.e("AddressName", "GetAddressName: $e")
        }
        return addressName
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayBg.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}