package com.project.capstoneapp.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.MessageResponse
import com.project.capstoneapp.data.remote.response.UserResponse
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel(private val mRepository: Repository) : ViewModel() {
    val userResponse: LiveData<UserResponse?> = mRepository.userResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    val messageResponse: LiveData<MessageResponse?> = mRepository.messageResponse

    fun getLoginSession() = mRepository.getLoginSession()

    suspend fun getUserById() {
        mRepository.getUserById()
    }

    fun editLoginSession(
        user: UserResponse,
        calorieNeeds: Double
    ) {
        viewModelScope.launch {
            mRepository.editLoginSession(user, calorieNeeds)
        }
    }

    fun editProfile(weightKg: Double, heightCm: Double, imageFile: File?) {
        viewModelScope.launch {
            mRepository.editProfile(weightKg, heightCm, imageFile)
        }
    }

    companion object {
        const val TAG = "EditProfileViewModel"
    }
}