package com.project.capstoneapp.ui.main.exercise.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.response.ExerciseHistoryResponse
import com.project.capstoneapp.data.remote.response.TrackingResponse

class TrackingViewModel (
    private val mRepository: Repository
) : ViewModel(){
    val trackingResponse: LiveData<List<TrackingResponse>?> = mRepository.trackingResponse

    val exerciseHistoryResponse: LiveData<ExerciseHistoryResponse?> = mRepository.exerciseHistoryResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun getTrackingList() {
        mRepository.getTrackingList()
    }

    suspend fun addExerciseHistory(type: String, name: String, durationMinutes: Int, calorie: Float, createdAt: String) {
        mRepository.addExerciseHistory(type, name, durationMinutes, calorie, createdAt)
    }

    companion object {
        const val TAG = "TrackingViewModel"
    }
}