package com.project.capstoneapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.project.capstoneapp.R
import com.project.capstoneapp.databinding.ActivityBottomMainBinding
import com.project.capstoneapp.ui.camera.CalculateActivity
import com.project.capstoneapp.ui.camera.ScanActivity
import com.project.capstoneapp.ui.onboarding.OnboardingActivity


class BottomMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_bottom_main)
        navView.setupWithNavController(navController)

        binding.navView.menu.getItem(2).isEnabled = false

        binding.navView.setOnItemSelectedListener { item ->
            // In order to get the expected behavior, you have to call default Navigation method manually
            NavigationUI.onNavDestinationSelected(item, navController)

            return@setOnItemSelectedListener false
        }

        setListeners()
        setNavigation()
    }

    private fun setListeners() {
        binding.btnScan.setOnClickListener {
            val intentToScanActivity = Intent(this, ScanActivity::class.java)
            startActivity(intentToScanActivity)
        }
    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColorBlue)
    }

    private fun setNavigation() {
        when(intent.getStringExtra(EXTRA_NAVIGATION)) {
            "history" -> {
                val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)
                val menuItemId = R.id.navigation_history
                bottomNavigationView.selectedItemId = menuItemId
            }
        }
    }
    companion object {
        const val EXTRA_NAVIGATION = "extra_navigation"
    }
}