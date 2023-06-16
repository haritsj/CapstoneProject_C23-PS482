package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class ExerciseHistoryRequest (
    @field: SerializedName("jenis")
    val jenis: String,

    @field: SerializedName("name")
    val name: String,

    @field: SerializedName("durasi_menit")
    val durationMinutes: Int,

    @field: SerializedName("calorie")
    val calorie: Float,

    @field: SerializedName("created_at")
    val createdAt: String
)