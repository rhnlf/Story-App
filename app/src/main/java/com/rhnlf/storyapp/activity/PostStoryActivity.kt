package com.rhnlf.storyapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rhnlf.storyapp.R
import com.rhnlf.storyapp.activity.CameraActivity.Companion.EXTRA_IS_BACK_CAMERA
import com.rhnlf.storyapp.activity.CameraActivity.Companion.EXTRA_PICTURE
import com.rhnlf.storyapp.data.local.UserPreference
import com.rhnlf.storyapp.databinding.ActivityPostStoryBinding
import com.rhnlf.storyapp.helper.Helper.Companion.bitmapToFile
import com.rhnlf.storyapp.helper.Helper.Companion.dataStore
import com.rhnlf.storyapp.helper.Helper.Companion.rotateBitmap
import com.rhnlf.storyapp.helper.Helper.Companion.uriToFile
import com.rhnlf.storyapp.view.PostStoryViewModel
import com.rhnlf.storyapp.view.ViewModelFactory
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var viewModel: PostStoryViewModel

    private var getFile: File? = null
    private var token: String? = null

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.failed_to_get_permission), Toast.LENGTH_SHORT
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
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.post_story)
        supportActionBar?.setBackgroundDrawable(getColor(R.color.red).toDrawable())

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        initViewModel()
        setupAction()
        setButton()
    }

    override fun onResume() {
        super.onResume()
        setButton()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[PostStoryViewModel::class.java]

        viewModel.apply {
            isLoading.observe(this@PostStoryActivity) { isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@PostStoryActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT
                    ).setBackgroundTint(
                        ContextCompat.getColor(
                            this@PostStoryActivity, R.color.red_light
                        )
                    ).setTextColor(
                        ContextCompat.getColor(
                            this@PostStoryActivity, R.color.black
                        )
                    ).show()
                }
            }

            getUser().observe(this@PostStoryActivity) { user ->
                token = "Bearer ${user.token}"
            }

            tempFile.observe(this@PostStoryActivity) { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                binding.ivPreview.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener {
                startCameraX()
            }

            btnGallery.setOnClickListener {
                startGallery()
            }

            btnUpload.setOnClickListener {
                viewModel.uploadStory(
                    token as String, binding.edAddDescription.text.toString()
                )
            }

            edAddDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButton()
                }

                override fun afterTextChanged(s: Editable?) {
                    edAddDescription.error = if (edAddDescription.text.toString().isEmpty()) {
                        getString(R.string.this_field_cannot_be_blank)
                    } else null
                }
            })
        }
    }

    private fun setButton() {
        val image = binding.ivPreview.drawable
        val description = binding.edAddDescription.text

        binding.btnUpload.isEnabled =
            image != null && description != null && description.toString().isNotEmpty()
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(EXTRA_PICTURE, File::class.java)
            } else {
                @Suppress("DEPRECATION") it.data?.getSerializableExtra(EXTRA_PICTURE)
            } as? File

            val isBackCamera = it.data?.getBooleanExtra(EXTRA_IS_BACK_CAMERA, true) as Boolean
            val rotate = rotateBitmap(BitmapFactory.decodeFile(myFile?.path), isBackCamera)
            val result = bitmapToFile(this, rotate)
            viewModel.setFile(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostStoryActivity)
                getFile = myFile
                viewModel.setFile(myFile)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            val intent = Intent(this@PostStoryActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}