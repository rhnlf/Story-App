package com.rhnlf.storyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.activity.CameraActivity.Companion.EXTRA_IS_BACK_CAMERA
import com.rhnlf.storyapp.activity.CameraActivity.Companion.EXTRA_PICTURE
import com.rhnlf.storyapp.databinding.ActivityPostStoryBinding
import com.rhnlf.storyapp.helper.Helper.Companion.bitmapToFile
import com.rhnlf.storyapp.helper.Helper.Companion.rotateBitmap
import com.rhnlf.storyapp.helper.Helper.Companion.uriToFile
import com.rhnlf.storyapp.helper.ScreenState
import com.rhnlf.storyapp.view.PostStoryViewModel
import com.rhnlf.storyapp.view.ViewModelFactory
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var viewModel: PostStoryViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var getFile: File? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private var locationToggle: Boolean = false

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(EXTRA_PICTURE, File::class.java)
            } else {
                it.data?.getSerializableExtra(EXTRA_PICTURE)
            } as File

            val isBackCamera = it.data?.getBooleanExtra(EXTRA_IS_BACK_CAMERA, true) as Boolean
            val bmp = rotateBitmap(
                BitmapFactory.decodeFile(file.path), isBackCamera
            )
            val result = bitmapToFile(this, bmp)
            getFile = result
            viewModel.setFile(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val file = uriToFile(selectedImg, this)
            getFile = file
            viewModel.setFile(file)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) requestPermission()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        title = getString(R.string.post_story)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())

        initViewModel()
        setupAction()
        setButton()
    }

    override fun onResume() {
        super.onResume()
        setButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.failed_to_get_permission), Toast.LENGTH_SHORT
                ).show()
                showPermissionDialog()
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    Log.e(TAG, "Lokasi : $lat, $lon")
                }
            }
        } else {
            requestPermissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(StringBuilder("\"${getString(R.string.app_name)}\" ").append(getString(R.string.camera_request_title)))
            setMessage(getString(R.string.camera_request_msg))
            setCancelable(false)
            setPositiveButton(R.string.yes) { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            setNegativeButton(getString(R.string.no)) { dialogInterface, _ -> dialogInterface.dismiss() }
            show()
        }
    }

    private fun requestPermission(): Boolean {
        val isGranted = allPermissionsGranted()
        if (!isGranted) ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
        return isGranted
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[PostStoryViewModel::class.java]

        viewModel.apply {
            tempFile.observe(this@PostStoryActivity) { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                binding.ivPreview.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener {
                requestPermission()
                if (allPermissionsGranted()) startCameraX()
            }

            edAddDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    c: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(c: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edAddDescription.error = if (edAddDescription.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }
            })

            btnUpload.setOnClickListener {
                val file = getFile as File
                val desc = edAddDescription.text.toString()
                if (!locationToggle) {
                    lat = null
                    lon = null
                } else getMyLastLocation()

                Log.e(TAG, "LOKASI TOGGLE: $locationToggle, $lat, $lon")
                viewModel.uploadStory(file, desc, lat = lat, lon = lon)
                    .observe(this@PostStoryActivity) { state ->
                        when (state) {
                            is ScreenState.Loading -> {
                                showLoading(true)
                            }

                            is ScreenState.Success -> {
                                showLoading(false)
                                val intent =
                                    Intent(this@PostStoryActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            is ScreenState.Error -> {
                                showLoading(false)
                                Log.e(TAG, "onError: ${state.data?.message}")
                                showError()
                            }

                            else -> {}
                        }
                    }
            }

            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) getMyLastLocation()
                locationToggle = isChecked
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun setButton() {
        val storyImage = binding.ivPreview.drawable
        val description = binding.edAddDescription.text

        binding.btnUpload.isEnabled =
            storyImage != null && description != null && description.toString().isNotEmpty()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showError() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error))
            setCancelable(false)
            setMessage(getString(R.string.something_went_wrong))
            setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            }
            show()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TAG = "AddStoryActivity"
    }
}