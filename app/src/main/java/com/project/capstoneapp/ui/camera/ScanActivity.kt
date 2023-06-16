package com.project.capstoneapp.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.utils.createFile
import com.dicoding.mystoryapp.utils.uriToFile
import com.project.capstoneapp.R
import com.project.capstoneapp.databinding.ActivityScanBinding
import com.project.capstoneapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var scanViewModel: ScanViewModel

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private var isFlashOn: Boolean = false

    private var isCalculateActivityOpened: Boolean = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT
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
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.layoutAppBar)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onStop() {
        super.onStop()
        isCalculateActivityOpened = false
    }

    override fun onResume() {
        super.onResume()
        isCalculateActivityOpened = false
        setStatusBarColor()

        if (allPermissionsGranted()) {
            setViewModel()
            startCamera()
        }
        setListeners()
    }

    private fun setViewModel() {
        scanViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[ScanViewModel::class.java]

        scanViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().apply {
                setFlashMode(if (isFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
            }.build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this,
                    getString(R.string.camera_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@ScanActivity,
                        getString(R.string.capture_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    lifecycleScope.launch {
                        scanViewModel.scan(photoFile)
                        val intentToCalculate =
                            Intent(this@ScanActivity, CalculateActivity::class.java)
                        intentToCalculate.putExtra(
                            CalculateActivity.EXTRA_PHOTO_RESULT,
                            photoFile
                        )
                        intentToCalculate.putExtra(CalculateActivity.EXTRA_SOURCE, "camera")
                        intentToCalculate.putExtra(
                            CalculateActivity.EXTRA_IS_BACK_CAMERA,
                            cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                        )
                        setResult(CalculateActivity.CAMERA_X_RESULT, intentToCalculate)
                        intentToCalculate.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                        startActivity(intentToCalculate)
                        isCalculateActivityOpened = true
                    }
                }
            }
        )
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            lifecycleScope.launch {
                scanViewModel.scan(myFile)
                val intentToCalculate = Intent(this@ScanActivity, CalculateActivity::class.java)
                intentToCalculate.putExtra(CalculateActivity.EXTRA_PHOTO_RESULT, myFile)
                intentToCalculate.putExtra(CalculateActivity.EXTRA_SOURCE, "gallery")
                intentToCalculate.putExtra(CalculateActivity.EXTRA_PHOTO_URI, selectedImg)
                intentToCalculate.putExtra(
                    CalculateActivity.EXTRA_IS_BACK_CAMERA,
                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                )
                setResult(CalculateActivity.CAMERA_X_RESULT, intentToCalculate)
                intentToCalculate.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                startActivity(intentToCalculate)
                isCalculateActivityOpened = true
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            layoutAppBar.setNavigationOnClickListener {
                finish()
            }

            binding.btnSwitch.setOnClickListener {
                cameraSelector =
                    if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }

            btnFlash.setOnClickListener {
                isFlashOn = !isFlashOn

                val flashMode = if (isFlashOn) {
                    ImageCapture.FLASH_MODE_ON
                } else {
                    ImageCapture.FLASH_MODE_OFF
                }

                imageCapture?.flashMode = flashMode

                if (isFlashOn) {
                    binding.btnFlash.setIconResource(R.drawable.ic_flash_on)
                } else {
                    binding.btnFlash.setIconResource(R.drawable.ic_flash_off)
                }
            }

            binding.btnCapture.setOnClickListener { takePhoto() }

            binding.btnGallery.setOnClickListener { startGallery() }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setStatusBarColor() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        val window: Window = this.window
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}