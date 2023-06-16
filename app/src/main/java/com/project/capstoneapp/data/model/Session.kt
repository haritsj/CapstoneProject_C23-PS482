package com.project.capstoneapp.data.model

import com.project.capstoneapp.data.remote.response.UserResponse

data class Session(
    val userId: String,
    val token: String,
    var isLogin: Boolean,
    var user: UserResponse? = null,
    var calorieNeeds: Double? = 0.0
)