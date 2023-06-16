package com.project.capstoneapp.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.FoodHistoryResponse
import com.project.capstoneapp.data.remote.response.FoodResponse
import com.project.capstoneapp.data.remote.response.ScanResponse
import java.io.File

class CalculateViewModel(
    private val mRepository: Repository
) : ViewModel() {
    val scanResponse: LiveData<ScanResponse?> = mRepository.scanResponse

    val foodResponse: LiveData<List<FoodResponse>?> = mRepository.foodResponse

    val foodHistoryResponse: LiveData<FoodHistoryResponse?> = mRepository.foodHistoryResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun getFoodList(hasil: String) {
        mRepository.getFoodList(hasil)
    }

    suspend fun addFoodHistory(
        jenis: String,
        name: String,
        restaurant: String,
        menu: String,
        quantity: Int,
        calorie: Float,
        createdAt: String,
        imageFile: File?
    ) {
        mRepository.addFoodHistory(
            jenis,
            name,
            restaurant,
            menu,
            quantity,
            calorie,
            createdAt,
            imageFile
        )
    }

    companion object {
        const val TAG = "CalculateViewModel"
    }
}