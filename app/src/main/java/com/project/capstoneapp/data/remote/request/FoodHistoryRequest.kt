package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class FoodHistoryRequest(
    @field:SerializedName("jenis")
    val jenis: String?,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("restaurant")
    val restaurant: String?,

    @field:SerializedName("menu")
    val menu: String?,

    @field:SerializedName("quantity")
    val quantity: Int?,

    @field:SerializedName("calorie")
    val calorie: Float?,

    @field:SerializedName("created_at")
    val createdAt: String?,
)
