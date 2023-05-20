package com.devcode.storyapp.ui.addStory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyapp.*
import com.devcode.storyapp.databinding.ActivityAddStoryBinding
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.cameraActivity.CameraActivity
import com.devcode.storyapp.ui.home.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.devcode.storyapp.utils.Result
import java.io.File


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var userToken: String
    private lateinit var factory: ViewModelFactory
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var isCheck: Boolean = false
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
                    "Tidak mendapatkan permission.",
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
        switchUser(isCheck)
        setupAction()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
        addStoryViewModel.getUser().observe(this) {
            userToken = it.token
        }
    }

    private fun switchUser(isCheck: Boolean){
        binding.apply {
            switchUser.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                this@AddStoryActivity.isCheck = isChecked != isCheck
                Log.d("CheckingState", "onCreate: ${this@AddStoryActivity}")
            }
            switchLocation.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                this@AddStoryActivity.isCheck = isChecked != isCheck
                Log.d("CheckingState", "onCreate: ${this@AddStoryActivity}")
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
                    setMessage("Gambar dan Description tidak boleh kosong")
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            }
            uploadImage()
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
                    Snackbar.make(
                        binding.root,
                        "Your Location is - \nLat: $lat\nLong: $lon",
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
        val chooser = Intent.createChooser(intent, "Choose a Picture")
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
            addStoryViewModel.addStory(token, imageMultipart, description, lat, lon).observe(this) {
                when (it) {
                    is Result.Success -> {
                        showLoading(false)
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Upload Succes", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Loading -> {
                        showLoading(true)
                        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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