package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class RecommendationRequest (
    @field:SerializedName("calorie_kcal")
    val calorieKcal: Int,

    @field:SerializedName("exercise_time_minute")
    val exerciseMinute: Int,

    @field:SerializedName("user_weight_kg")
    val userWeight: Int
)