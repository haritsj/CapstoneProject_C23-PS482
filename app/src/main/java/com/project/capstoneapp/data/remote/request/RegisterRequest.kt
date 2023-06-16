package com.project.capstoneapp.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("gender")
    val gender: String,

    @field:SerializedName("weight_kg")
    val weightKg: String,

    @field:SerializedName("height_cm")
    val heightCm: String,

    @field:SerializedName("birth_date")
    val birthDate: String
)
