package com.project.capstoneapp.ui.auth.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.capstoneapp.R
import com.project.capstoneapp.databinding.ActivitySecondRegisterBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.auth.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.*


class SecondRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        setForm()
        setListeners()
    }

    private fun setViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[RegisterViewModel::class.java]

        registerViewModel.registerResponse.observe(this) {
            if (it?.error == null) {
                val intentToLogin = Intent(this@SecondRegisterActivity, LoginActivity::class.java)
                startActivity(intentToLogin)
                finish()
            }
        }
        registerViewModel.toastText.observe(this) {
            var toastText = ""
            when (it?.toString()) {
                "User Created" -> toastText = getString(R.string.register_successful)
                "The email address is already in use by another account." -> toastText = getString(
                    R.string.register_invalid)
                "Request Timeout" -> toastText = getString(R.string.register_timeout)
                "Unable to resolve host \"bangkit-capstone-gar.ue.r.appspot.com\": No address associated with hostname" -> toastText = getString(
                    R.string.register_failed)
            }
            if (toastText.isNotEmpty()) {
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun register(name: String, email: String, password: String, gender: String, weightKg: String, heightCm: String, birthDate: String) {
        registerViewModel.register(name, email, password, gender, weightKg, heightCm, birthDate)
    }

    private fun setForm() {
        spinnerAdapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            GENDER_LIST
        )

        binding.spinnerGender.setAdapter(spinnerAdapter)

        calendar = Calendar.getInstance()

        val date =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, day: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateLabel()
            }

        binding.edDate.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = intent.getStringExtra(NAME).toString()
                val email = intent.getStringExtra(EMAIL).toString()
                val password = intent.getStringExtra(PASSWORD).toString()
                val gender = spinnerGender.text.toString()
                val weightKg = edWeight.text.toString()
                val heightCm = edHeight.text.toString()
                val birthDate = edDate.text.toString()
                register(name, email, password, gender, weightKg, heightCm, birthDate)
            }
        }
    }

    private fun updateDateLabel() {
        val myFormat = "MM/dd/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        binding.edDate.setText(dateFormat.format(calendar.time))
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        val GENDER_LIST = arrayOf("Men", "Women")
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}