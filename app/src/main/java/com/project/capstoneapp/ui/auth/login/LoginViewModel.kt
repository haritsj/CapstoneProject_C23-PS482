package com.project.capstoneapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val mRepository: Repository) : ViewModel() {
    val userResponse: LiveData<UserResponse?> = mRepository.userResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    suspend fun saveLoginSession(userId: String, userToken: String, user: UserResponse, calorieNeeds: Double) {
        mRepository.saveLoginSession(userId, userToken, user, calorieNeeds)
    }

    fun getFirebaseAuth(): FirebaseAuth = mRepository.getFirebaseAuth()

    fun login(email: String, password: String): Task<AuthResult> =
        mRepository.login(email, password)

    fun getUserById(userId: String, token: String) {
        viewModelScope.launch {
            mRepository.getUserById(userId, token)
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}