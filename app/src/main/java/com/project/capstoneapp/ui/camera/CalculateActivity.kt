package com.project.capstoneapp.ui.camera

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.utils.decodeBitmap
import com.dicoding.mystoryapp.utils.getDateWithUserLocaleFormat
import com.dicoding.mystoryapp.utils.rotateFile
import com.project.capstoneapp.R
import com.project.capstoneapp.data.remote.response.FoodResponse
import com.project.capstoneapp.databinding.ActivityCalculateBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.main.BottomMainActivity
import kotlinx.coroutines.launch
import java.io.File

class CalculateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculateBinding
    private lateinit var calculateViewModel: CalculateViewModel

    private var file: File? = null

    private lateinit var spinnerRestaurantAdapter: ArrayAdapter<String>
    private lateinit var spinnerFoodAdapter: ArrayAdapter<String>

    private var foodResponse = ArrayList<FoodResponse>()

    private var restaurantList = ArrayList<String>()

    private var foodList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()

        setSupportActionBar(binding.layoutAppBar)

        setStatusBarColor()
        setContent()
        setListeners()
    }

    private fun setViewModel() {
        calculateViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[CalculateViewModel::class.java]

        calculateViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        calculateViewModel.toastText.observe(this) {
            var toastText = ""
            when (it?.toString()) {
                "timeout" -> {
                    toastText = getString(R.string.scan_timeout)
                    finish()
                }
                "Unable to resolve host \"flask-c23-ps482-p7hhnsgwnq-uc.a.run.app\": No address associated with hostname" -> {
                    toastText = getString(R.string.scan_failed)
                    finish()
                }
                "Unable to resolve host \"bangkit-capstone-gar.ue.r.appspot.com\": No address associated with hostname" -> {
                    toastText = getString(R.string.scan_failed)
                    finish()
                }
            }
            if (toastText.isNotEmpty()) {
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }

        calculateViewModel.scanResponse.observe(this) {
            if (!it?.hasil.isNullOrEmpty()) {
                binding.tvFood.text = it?.hasil.toString()
                calculateViewModel.getFoodList(it?.hasil.toString())
            }
        }

        calculateViewModel.foodResponse.observe(this) {
            it?.let {
                foodResponse = ArrayList(it)
                val restaurantNames: List<String?> =
                    it.map { foodList -> foodList.company }.distinct()
                restaurantList = ArrayList(restaurantNames.filterNotNull())
                foodList = ArrayList(emptyList())

                setSpinnerItem()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setContent() {
        val myFile = intent.getSerializableExtra(EXTRA_PHOTO_RESULT) as File

        val isBackCamera = intent?.getBooleanExtra(EXTRA_IS_BACK_CAMERA, true) as Boolean

        if (intent.getStringExtra(EXTRA_SOURCE).equals("camera")) {
            val rotatedFile = rotateFile(myFile, isBackCamera, REQ_WIDTH, REQ_HEIGHT)
            binding.ivPreview.setImageBitmap(rotatedFile)
        } else {
            val rotatedFile = decodeBitmap(myFile, REQ_WIDTH, REQ_WIDTH)
            binding.ivPreview.setImageBitmap(rotatedFile)
        }

        file = myFile
    }

    private fun setSpinnerItem() {
        binding.apply {
            spinnerRestaurantAdapter = ArrayAdapter<String>(
                this@CalculateActivity,
                R.layout.dropdown_menu_popup_item,
                restaurantList
            )
            spinnerRestaurant.setAdapter(spinnerRestaurantAdapter)

            spinnerFoodAdapter = ArrayAdapter<String>(
                this@CalculateActivity,
                R.layout.dropdown_menu_popup_item,
                foodList
            )
            spinnerFood.setAdapter(spinnerFoodAdapter)
        }
    }

    private fun setListeners() {
        binding.apply {
            btnFinish.isEnabled = false

            spinnerRestaurant.addTextChangedListener(watcher)
            spinnerFood.addTextChangedListener(watcher)
            edQuantity.addTextChangedListener(watcher)

            spinnerRestaurant.addTextChangedListener { restaurantName ->
                spinnerFood.clearListSelection()
                spinnerFood.clearComposingText()
                val filteredFoodResponse = foodResponse
                    .filter { it.company == restaurantName.toString().trim() }
                    .distinct()
                if (filteredFoodResponse.isNotEmpty()) {
                    val menuList = filteredFoodResponse.map {
                        it.menu.toString()
                    }.distinct()
                    if (menuList.isNotEmpty()) {
                        foodList = ArrayList(menuList)
                        setSpinnerItem()
                    }
                }
            }

            layoutAppBar.setNavigationOnClickListener {
                @Suppress("DEPRECATION")
                val alertDialogBuilder = AlertDialog.Builder(this@CalculateActivity)
                alertDialogBuilder.setTitle(getString(R.string.title_discard_scan))
                    .setMessage(getString(R.string.message_discard_scan))
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.negative_discard_scan)) { _, _ ->
                    }
                    .setPositiveButton(getString(R.string.positive_discard_scan)) { _, _ ->
                        finish()
                    }.show()
            }

            btnFinish.setOnClickListener {
                if (spinnerFood.text.toString() == "Select Food" || spinnerRestaurant.text.toString() == "Select Restaurant") {
                    Toast.makeText(
                        this@CalculateActivity,
                        "Please select an exercise to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val jenis = "food"
                    val name = tvFood.text.toString()
                    val restaurant = spinnerRestaurant.text.toString()
                    val food = spinnerFood.text.toString()
                    val quantity = edQuantity.text.toString().toInt()
                    val calorie = foodResponse.filter {
                        it.company == restaurant && it.menu == food
                    }.map { it.calories }.first()
                    val createdAt = getDateWithUserLocaleFormat()
                    lifecycleScope.launch {
                        calculateViewModel.addFoodHistory(
                            jenis,
                            name,
                            restaurant,
                            food,
                            quantity,
                            calorie!!,
                            createdAt,
                            file
                        )
                        val intentToHome =
                            Intent(this@CalculateActivity, BottomMainActivity::class.java)
                        intentToHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        intentToHome.putExtra(BottomMainActivity.EXTRA_NAVIGATION, "history")
                        startActivity(intentToHome)
                    }
                }
            }

        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setButtonEnable()
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun setButtonEnable() {
        binding.apply {
            btnFinish.isEnabled =
                spinnerFood.text.toString() != "Select Food" && spinnerRestaurant.text.toString() != "Select Restaurant" && !edQuantity.text.isNullOrBlank()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    @Suppress("DEPRECATION")
    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorBlue)
    }

    companion object {
        const val EXTRA_SOURCE = "extra_source"
        const val EXTRA_IS_BACK_CAMERA = "extra_is_back_camera"
        const val EXTRA_PHOTO_RESULT = "extra_photo_result"
        const val EXTRA_PHOTO_URI = "extra_photo_uri"
        const val CAMERA_X_RESULT = 200
        const val REQ_WIDTH = 400
        const val REQ_HEIGHT = 300
    }
}