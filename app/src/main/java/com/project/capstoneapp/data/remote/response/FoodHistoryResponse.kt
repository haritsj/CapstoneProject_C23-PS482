package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class FoodHistoryResponseDeserializer : JsonDeserializer<FoodHistoryResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): FoodHistoryResponse {
        val jsonObject = json?.asJsonObject

        val id = jsonObject?.get("id")?.asString
        val jenis = jsonObject?.get("jenis")?.asString
        val name = jsonObject?.get("name")?.asString
        val restaurant = jsonObject?.get("restaurant")?.asString
        val menu = jsonObject?.get("menu")?.asString
        val quantity = jsonObject?.get("quantity")?.asInt
        val calorie = jsonObject?.get("calorie")?.asFloat
        val createdAt = jsonObject?.get("created_at")?.asString

        return FoodHistoryResponse(id, jenis, name, restaurant, menu, quantity, calorie, createdAt)
    }
}

data class FoodHistoryResponse(
    @field:SerializedName("id")
    val id: String?,

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