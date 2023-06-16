package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class FoodRequest(
    @field:SerializedName("hasil")
    val hasil: String
)
