package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class UserResponseDeserializer : JsonDeserializer<UserResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserResponse {
        val jsonObject = json?.asJsonObject
        val error = jsonObject?.get("error")?.asString
        val id = jsonObject?.get("id")?.asString
        val name = jsonObject?.get("name")?.asString
        val email = jsonObject?.get("email")?.asString
        val password = jsonObject?.get("password")?.asString
        val gender = jsonObject?.get("gender")?.asString
        val weightKg = jsonObject?.get("weight_kg")?.asDouble
        val heightCm = jsonObject?.get("height_cm")?.asDouble
        val birthDate = jsonObject?.get("birth_date")?.asString
        val photoUrl = jsonObject?.get("photoURL")?.asString

        return UserResponse(error, id, name, email, password, gender, weightKg, heightCm, birthDate, photoUrl)
    }
}

data class UserResponse(
    @field:SerializedName("error")
    val error: String?,

    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("email")
    val email: String? = "",

    @field:SerializedName("password")
    val password: String? = "",

    @field:SerializedName("gender")
    val gender: String? = "",

    @field:SerializedName("weight_kg")
    val weightKg: Double? = 0.0,

    @field:SerializedName("height_cm")
    val heightCm: Double? = 0.0,

    @field:SerializedName("birth_date")
    val birthDate: String? = "",

    @field:SerializedName("photoURL")
    val photoUrl: String? = ""
)
