package com.project.capstoneapp.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.RegisterResponse

class ProfileViewModel(private val mRepository: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun logout() {
        mRepository.logout()
    }

    fun getLoginSession() = mRepository.getLoginSession()

    suspend fun clearLoginSession() {
        mRepository.clearLoginSession()
    }

    companion object {
        const val TAG = "ProfileViewModel"
    }
}