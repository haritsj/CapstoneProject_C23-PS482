package com.project.capstoneapp.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.FoodResponse
import com.project.capstoneapp.data.remote.response.ScanResponse
import java.io.File

class ScanViewModel(
    private val mRepository: Repository
) : ViewModel() {
    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    companion object {
        const val TAG = "CalculateViewModel"
    }

    suspend fun scan(imageFile: File) {
        mRepository.scan(imageFile)
    }
}