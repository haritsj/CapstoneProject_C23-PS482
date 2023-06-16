package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class HistoryResponseDeserializer : JsonDeserializer<HistoryResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): HistoryResponse {
        val jsonObject = json?.asJsonObject

        val id = jsonObject?.get("id")?.asString
        val userId = jsonObject?.get("user_id")?.asString
        val durasiMenit = jsonObject?.get("durasi_menit")?.asInt
        val jenis = jsonObject?.get("jenis")?.asString
        val name = jsonObject?.get("name")?.asString
        val calorie = jsonObject?.get("calorie")?.asFloat
        val restaurant = jsonObject?.get("restaurant")?.asString
        val menu = jsonObject?.get("menu")?.asString
        val quantity = jsonObject?.get("quantity")?.asInt
        val createdAt = jsonObject?.get("created_at")?.asString

        return HistoryResponse(
            id,
            userId,
            jenis,
            name,
            restaurant,
            menu,
            quantity,
            durasiMenit,
            calorie,
            createdAt
        )
    }
}


data class HistoryResponse(
    @field:SerializedName("id")
    val id: String?,

    @field:SerializedName("user_id")
    val userId: String?,

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

    @field:SerializedName("durasi_menit")
    val durasiMenit: Int?,

    @field:SerializedName("calorie")
    var calorie: Float?,

    @field:SerializedName("created_at")
    val createdAt: String?
)

