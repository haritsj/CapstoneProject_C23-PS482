package com.project.capstoneapp.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.utils.*
import com.project.capstoneapp.R
import com.project.capstoneapp.databinding.ActivityEditProfileBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.camera.CalculateActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var editProfileViewModel: EditProfileViewModel

    private var imageFile: File? = null

    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()

        setSupportActionBar(binding.layoutAppBar)

        setStatusBarColor()
        setContent()
        setListeners()
    }

    private fun setViewModel() {
        editProfileViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[EditProfileViewModel::class.java]

        editProfileViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        editProfileViewModel.toastText.observe(this) {
            var toastText = ""
            when (it?.toString()) {
                "User successfully updated." -> {
                    toastText =
                        getString(R.string.edit_profile_successful)
                    this.finish()
                }

                "timeout" -> toastText = getString(R.string.edit_profile_timeout)
                "Unable to resolve host \"bangkit-capstone-gar.ue.r.appspot.com\": No address associated with hostname" -> toastText =
                    getString(
                        R.string.edit_profile_failed
                    )
            }
            if (toastText.isNotEmpty()) {
                android.widget.Toast.makeText(this, toastText, android.widget.Toast.LENGTH_SHORT)
                    .show()
            }
        }

        editProfileViewModel.messageResponse.observe(this) {
            it?.let {
                if (!it.message.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        editProfileViewModel.getUserById()
                        editProfileViewModel.userResponse.observe(this@EditProfileActivity) {user ->
                            if (user != null) {
                                val calorieNeeds = calculatedCalorieNeeds(
                                    user.gender!!,
                                    user.weightKg!!,
                                    user.heightCm!!,
                                    user.birthDate!!
                                )
                                editProfileViewModel.editLoginSession(user, calorieNeeds)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setContent() {
        editProfileViewModel.getLoginSession().observe(this) {
            val photoUrl = it.user?.photoUrl.toString()
            if (photoUrl.isNotEmpty() && photoUrl != "null") {
                Glide.with(this)
                    .load(photoUrl)
                    .fitCenter()
                    .into(binding.ivProfilePicture)
                this.imageUrl = photoUrl
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnSave.isEnabled = false

            edWeight.addTextChangedListener(watcher)
            edHeight.addTextChangedListener(watcher)

            tvChangeProfilePicture.setOnClickListener {
                startGallery()
            }

            layoutAppBar.setNavigationOnClickListener {
                finish()
            }

            binding.btnSave.setOnClickListener {

                val weightKg = binding.edWeight.text.toString().trim().toDouble()
                val heightCm = binding.edHeight.text.toString().trim().toDouble()

                showLoading(true)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        editProfileViewModel.editProfile(weightKg, heightCm, imageFile)
                    }

                    showLoading(false)
                }
            }
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.btnSave.isEnabled = !binding.edWeight.text.isNullOrBlank() && !binding.edHeight.text.isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
        }
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
            binding.ivProfilePicture.setImageURI(selectedImg)
            imageFile = null

            val context = this@EditProfileActivity
            lifecycleScope.launch {
                val decodedFile = withContext(Dispatchers.Default) {
                    uriToFile(selectedImg, context)?.let {
                        decodeBitmap(
                            it,
                            CalculateActivity.REQ_WIDTH,
                            CalculateActivity.REQ_WIDTH
                        )
                    }
                }
                decodedFile?.let {
                    binding.ivProfilePicture.setImageBitmap(it)
                    imageFile = uriToFile(selectedImg, context)
                }
            }
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
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorBlue)
    }
}