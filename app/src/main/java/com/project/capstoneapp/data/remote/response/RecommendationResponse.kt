package com.project.capstoneapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class RecommendationResponse(
    @field:SerializedName("exercise")
    val exercise: String?,

    @field:SerializedName("calorie")
    val calorie: Float?,

    @field:SerializedName("time_minute")
    val timeMinute: Int?,
)