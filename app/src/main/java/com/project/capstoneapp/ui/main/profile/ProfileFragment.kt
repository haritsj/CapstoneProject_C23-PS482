package com.project.capstoneapp.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.project.capstoneapp.R
import com.project.capstoneapp.databinding.FragmentProfileBinding
import com.project.capstoneapp.ui.ViewModelFactory
import com.project.capstoneapp.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var _profileViewModel: ProfileViewModel? = null
    private val profileViewModel get() = _profileViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setContent()
        setListeners()
    }

    private fun setViewModel() {
        _profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[ProfileViewModel::class.java]

        profileViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setContent() {
        binding.apply {
            profileViewModel.getLoginSession().observe(this@ProfileFragment) {
                tvName.text = it.user?.name.toString()
                tvEmail.text = it.user?.email.toString()
                val photoUrl = it.user?.photoUrl.toString()
                if (photoUrl.isNotEmpty() && photoUrl != "null") {
                    Glide.with(this@ProfileFragment)
                        .load(photoUrl)
                        .fitCenter()
                        .into(binding.ivPhoto)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnEditProfile.setOnClickListener {
                val intentToEdit = Intent(requireContext(), EditProfileActivity::class.java)
                startActivity(intentToEdit)
            }

            btnLogout.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.message_logout))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.negative_logout)) { _, _ ->
                }
                .setPositiveButton(getString(R.string.logout)) { _, _ ->
                    profileViewModel.logout()
                    lifecycleScope.launch {
                        profileViewModel.clearLoginSession()
                    }
                    val intentToOnboarding = Intent(requireContext(), OnboardingActivity::class.java)
                    requireActivity().finishAffinity()
                    startActivity(intentToOnboarding)
                    Toast.makeText(
                        context,
                        getString(R.string.logout_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        Glide.with(this).clear(binding.ivPhoto)
        _binding = null
    }
}