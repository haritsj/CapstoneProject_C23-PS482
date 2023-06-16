package com.project.capstoneapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.RegisterResponse

class RegisterViewModel(private val mRepository: Repository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse?> = mRepository.registerResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun register(name: String, email: String, password: String, gender: String, weightKg: String, heightCm: String, birthDate: String) {
        mRepository.register(name, email, password, gender, weightKg, heightCm, birthDate)
    }

    companion object {
        const val TAG = "RegisterViewModel"
    }
}