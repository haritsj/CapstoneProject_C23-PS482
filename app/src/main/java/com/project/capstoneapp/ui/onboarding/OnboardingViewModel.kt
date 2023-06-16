package com.project.capstoneapp.ui.onboarding

import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository

class OnboardingViewModel(private val mRepository: Repository) : ViewModel() {
    fun getFirebaseAuth() = mRepository.getFirebaseAuth()

    companion object {
        const val TAG = "OnboardingViewModel"
    }
}