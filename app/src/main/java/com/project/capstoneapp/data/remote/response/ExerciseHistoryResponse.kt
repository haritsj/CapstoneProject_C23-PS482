package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class ExerciseHistoryResponseDeserializer : JsonDeserializer<ExerciseHistoryResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ExerciseHistoryResponse {
        val jsonObject = json?.asJsonObject

        val id = jsonObject?.get("id")?.asString
        val jenis = jsonObject?.get("jenis")?.asString
        val name = jsonObject?.get("name")?.asString
        val durasiMenit = jsonObject?.get("durasi_menit")?.asInt
        val calorie = jsonObject?.get("calorie")?.asFloat
        val createdAt = jsonObject?.get("created_at")?.asString

        return ExerciseHistoryResponse(id, jenis, name, durasiMenit, calorie, createdAt)
    }
}

data class ExerciseHistoryResponse(
    @field:SerializedName("id")
    val id: String?,

    @field:SerializedName("jenis")
    val jenis: String?,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("durasi_menit")
    val durasiMenit: Int?,

    @field:SerializedName("calorie")
    val calorie: Float?,

    @field:SerializedName("created_at")
    val createdAt: String?
)