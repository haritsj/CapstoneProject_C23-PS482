package com.project.capstoneapp.data.remote.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

class ScanResponseDeserializer : JsonDeserializer<ScanResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ScanResponse {
        val jsonObject = json?.asJsonObject
        val hasil = jsonObject?.get("hasil")?.asString
        val error = jsonObject?.get("error")?.asString

        return ScanResponse(hasil, error)
    }
}

data class ScanResponse(
    @field:SerializedName("hasil")
    val hasil: String?,

    @field:SerializedName("error")
    val error: String?
)