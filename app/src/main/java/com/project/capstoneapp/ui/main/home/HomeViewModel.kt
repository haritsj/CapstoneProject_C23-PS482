package com.project.capstoneapp.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.model.Session
import com.project.capstoneapp.data.remote.response.HistoryResponse

class HomeViewModel(private val mRepository: Repository) : ViewModel() {

    val historyResponse: LiveData<List<HistoryResponse>?> = mRepository.historyResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun getLoginSession() : LiveData<Session> = mRepository.getLoginSession()

    suspend fun getAllHistory(userId: String, token: String) {
        mRepository.getAllHistory(userId, token)
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}