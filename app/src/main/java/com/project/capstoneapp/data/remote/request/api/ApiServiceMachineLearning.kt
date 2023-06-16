package com.project.capstoneapp.data.remote.request.api

import com.project.capstoneapp.data.remote.response.ScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceMachineLearning {
    @Multipart
    @POST("api/junkfood")
    suspend fun scan(
        @Header("Authorization") token: String,
        @Part image : MultipartBody.Part
    ): Response<ScanResponse>
}