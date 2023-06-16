package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class EditProfile(
    @field:SerializedName("weight_kg")
    val weightKg: Double,

    @field:SerializedName("height_cm")
    val heightCm: Double
)