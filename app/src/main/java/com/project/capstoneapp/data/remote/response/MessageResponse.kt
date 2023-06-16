package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class MessageResponseDeserializer : JsonDeserializer<MessageResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MessageResponse {
        val jsonObject = json?.asJsonObject
        val error = jsonObject?.get("error")?.asString
        val message = jsonObject?.get("message")?.asString

        return MessageResponse(error, message)
    }
}

data class MessageResponse(
    @field:SerializedName("error")
    val error: String?,

    @field:SerializedName("message")
    val message: String?
)
