package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class RegisterResponseDeserializer : JsonDeserializer<RegisterResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): RegisterResponse {
        val jsonObject = json?.asJsonObject
        val error = jsonObject?.get("error")?.asString
        val name = jsonObject?.get("name")?.asString
        val email = jsonObject?.get("email")?.asString
        val password = jsonObject?.get("password")?.asString
        val gender = jsonObject?.get("gender")?.asString
        val weightKg = jsonObject?.get("weight_kg")?.asString
        val heightCm = jsonObject?.get("height_cm")?.asString
        val birthDate = jsonObject?.get("birth_date")?.asString

        return RegisterResponse(error, name, email, password, gender, weightKg, heightCm, birthDate)
    }
}

data class RegisterResponse(
    @field:SerializedName("error")
    val error: String?,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("email")
    val email: String?,

    @field:SerializedName("password")
    val password: String?,

    @field:SerializedName("gender")
    val gender: String?,

    @field:SerializedName("weight_kg")
    val weightKg: String?,

    @field:SerializedName("height_cm")
    val heightCm: String?,

    @field:SerializedName("birth_date")
    val birthDate: String?
)