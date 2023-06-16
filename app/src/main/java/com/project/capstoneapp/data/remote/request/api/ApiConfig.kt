package com.project.capstoneapp.data.remote.request.api

import androidx.viewbinding.BuildConfig
import com.google.gson.GsonBuilder
import com.project.capstoneapp.data.remote.response.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private const val BASE_URL_MAIN = "https://bangkit-capstone-gar.ue.r.appspot.com/"
        private const val BASE_URL_ML = "https://flask-c23-ps482-p7hhnsgwnq-uc.a.run.app/"

        fun getApiServiceMain() : ApiServiceMain {
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val gson = GsonBuilder()
                .registerTypeAdapter(RegisterResponse::class.java, RegisterResponseDeserializer())
                .registerTypeAdapter(UserResponse::class.java, UserResponseDeserializer())
                .registerTypeAdapter(FoodHistoryResponse::class.java, FoodHistoryResponseDeserializer())
                .registerTypeAdapter(ExerciseHistoryResponse::class.java, ExerciseHistoryResponseDeserializer())
                .registerTypeAdapter(MessageResponse::class.java, MessageResponseDeserializer())
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_MAIN)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
            return retrofit.create(ApiServiceMain::class.java)
        }

        fun getApiServiceMachineLearning() : ApiServiceMachineLearning {
            val loggingInterceptor = if(BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val gson = GsonBuilder()
                .registerTypeAdapter(ScanResponse::class.java, ScanResponseDeserializer())
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_ML)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
            return retrofit.create(ApiServiceMachineLearning::class.java)
        }
    }
}