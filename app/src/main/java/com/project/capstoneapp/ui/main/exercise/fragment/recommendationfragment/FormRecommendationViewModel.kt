package com.project.capstoneapp.ui.main.exercise.fragment.recommendationfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.remote.request.RecommendationRequest
import com.project.capstoneapp.data.remote.response.RecommendationResponse

class FormRecommendationViewModel(
    private val mRepository: Repository
): ViewModel() {
    val recommendationResponse: LiveData<List<RecommendationResponse>?> = mRepository.recommendationResponse

    val isLoading: LiveData<Boolean> = mRepository.isLoading

    val toastText: LiveData<String> = mRepository.toastText

    fun getRecommendationList(recommendationRequest: RecommendationRequest) {
        mRepository.getRecommendationList(recommendationRequest)
    }

    fun getLoginSession() = mRepository.getLoginSession()

    companion object {
        const val TAG = "RecommendationViewModel"
    }
}