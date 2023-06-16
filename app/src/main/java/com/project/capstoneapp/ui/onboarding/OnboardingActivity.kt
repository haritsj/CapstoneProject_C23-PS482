package com.project.capstoneapp.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.capstoneapp.databinding.ActivityOnboardingBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.auth.login.LoginActivity
import com.project.capstoneapp.ui.auth.register.FirstRegisterActivity
import com.project.capstoneapp.ui.main.BottomMainActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingViewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        checkLoginSession()
        setListeners()
    }

    private fun setViewModel() {
        onboardingViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[OnboardingViewModel::class.java]
    }

    private fun checkLoginSession() {
        if (onboardingViewModel.getFirebaseAuth().currentUser != null) {
            Intent(
                this@OnboardingActivity,
                BottomMainActivity::class.java
            ).also { intentToMain ->
                intentToMain.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentToMain)
                finish()
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                val iRegister = Intent(this@OnboardingActivity, FirstRegisterActivity::class.java)
                startActivity(iRegister)
            }

            btnLogin.setOnClickListener {
                val iLogin = Intent(this@OnboardingActivity, LoginActivity::class.java)
                startActivity(iLogin)
            }
        }
    }
}