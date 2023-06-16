package com.project.capstoneapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class TrackingResponse(
    @field: SerializedName("judul")
    val judul: String?,

    @field: SerializedName("calorie_per_kg")
    val calorie_per_kg: Float?,

    @field: SerializedName("intensity_description")
    val intensity_description: String?
)