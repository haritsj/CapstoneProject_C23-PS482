package com.project.capstoneapp.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.project.capstoneapp.data.datastore.SettingsPreferences
import com.project.capstoneapp.data.model.Session
import com.project.capstoneapp.data.remote.request.FoodRequest
import com.project.capstoneapp.data.remote.request.RecommendationRequest
import com.project.capstoneapp.data.remote.request.RegisterRequest
import com.project.capstoneapp.data.remote.request.api.ApiServiceMachineLearning
import com.project.capstoneapp.data.remote.request.api.ApiServiceMain
import com.project.capstoneapp.data.remote.response.*
import com.project.capstoneapp.ui.auth.login.LoginViewModel
import com.project.capstoneapp.ui.auth.register.RegisterViewModel
import com.project.capstoneapp.ui.camera.CalculateViewModel
import com.project.capstoneapp.ui.main.exercise.fragment.TrackingViewModel
import com.project.capstoneapp.ui.main.exercise.fragment.recommendationfragment.FormRecommendationViewModel
import com.project.capstoneapp.ui.main.profile.EditProfileViewModel
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.net.SocketTimeoutException

class Repository(
    private val settingsPreferences: SettingsPreferences,
    private val apiServiceMain: ApiServiceMain,
    private val apiServiceMachineLearning: ApiServiceMachineLearning
) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _registerResponse = MutableLiveData<RegisterResponse?>()
    val registerResponse: LiveData<RegisterResponse?> = _registerResponse

    private val _userResponse = MutableLiveData<UserResponse?>()
    val userResponse: LiveData<UserResponse?> = _userResponse

    private val _scanResponse = MutableLiveData<ScanResponse?>()
    val scanResponse: LiveData<ScanResponse?> = _scanResponse

    private val _foodResponse = MutableLiveData<List<FoodResponse>?>()
    val foodResponse: LiveData<List<FoodResponse>?> = _foodResponse

    private val _trackingResponse = MutableLiveData<List<TrackingResponse>?>()
    val trackingResponse: LiveData<List<TrackingResponse>?> = _trackingResponse

    private val _recommendationResponse = MutableLiveData<List<RecommendationResponse>?>()
    val recommendationResponse: LiveData<List<RecommendationResponse>?> = _recommendationResponse

    private val _foodHistoryResponse = MutableLiveData<FoodHistoryResponse?>()
    val foodHistoryResponse: LiveData<FoodHistoryResponse?> = _foodHistoryResponse

    private val _exerciseHistoryResponse = MutableLiveData<ExerciseHistoryResponse?>()
    val exerciseHistoryResponse: LiveData<ExerciseHistoryResponse?> = _exerciseHistoryResponse

    private val _historyResponse = MutableLiveData<List<HistoryResponse>?>()
    val historyResponse: LiveData<List<HistoryResponse>?> = _historyResponse

    private val _messageResponse = MutableLiveData<MessageResponse?>()
    val messageResponse: LiveData<MessageResponse?> = _messageResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String> = _toastText

    fun register(
        name: String,
        email: String,
        password: String,
        gender: String,
        weightKg: String,
        heightCm: String,
        birthDate: String
    ) {
        _isLoading.value = true
        _toastText.value = ""
        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password,
            gender = gender,
            weightKg = weightKg,
            heightCm = heightCm,
            birthDate = birthDate
        )
        try {
            apiServiceMain.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.error != null) {
                            _toastText.value =
                                "The email address is already in use by another account."
                            Log.e(RegisterViewModel.TAG, "onFailure: ${registerResponse.error}")
                        } else {
                            _registerResponse.value = registerResponse
                            _toastText.value = "User Created"
                            Log.d(RegisterViewModel.TAG, "onSuccess: User Created")
                        }
                    } else {
                        _toastText.value = response.message()
                        Log.e(RegisterViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _isLoading.value = false
                    _toastText.value = t.message.toString()
                    Log.e(RegisterViewModel.TAG, "onFailure: ${t.message.toString()}")
                    if (t is HttpException) {
                        val errorResponse = t.response()?.errorBody()?.string()
                        _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                        Log.e(
                            RegisterViewModel.TAG,
                            "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                        )
                    } else if (t is SocketTimeoutException) {
                        _toastText.value = "Request timeout. Please try again later."
                        Log.e(RegisterViewModel.TAG, "onFailure: Request timeout", t)
                    }
                }
            })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(RegisterViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    fun login(email: String, password: String): Task<AuthResult> {
        _isLoading.value = true
        _toastText.value = ""
        return auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _isLoading.value = false
                _toastText.value = "Login Success"
                Log.e(
                    LoginViewModel.TAG,
                    "onSuccess: ${task.result}"
                )
            } else {
                _isLoading.value = false
                _toastText.value = task.exception?.message.toString()
                Log.e(
                    LoginViewModel.TAG,
                    "onFailure: ${task.exception?.message.toString()}"
                )
            }
        }
    }

    fun getUserById(userId: String, token: String) {
        _isLoading.value = true
        _toastText.value = ""
        try {
            apiServiceMain.getUserById(userId, "Bearer $token")
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse?.error != null) {
                                _toastText.value = userResponse.error.toString()
                                Log.e(
                                    LoginViewModel.TAG,
                                    "onFailure: ${userResponse.error}"
                                )
                            } else {
                                _userResponse.value = userResponse
                                _toastText.value = "User Found"
                                Log.d(
                                    LoginViewModel.TAG,
                                    "onSuccess: User Found ${response.body()?.name.toString()}"
                                )
                            }
                        } else {
                            _toastText.value = "Find User request failed"
                            Log.e(LoginViewModel.TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        _isLoading.value = false
                        _toastText.value = t.message.toString()
                        Log.e(LoginViewModel.TAG, "onFailure: ${t.message.toString()}")
                        if (t is HttpException) {
                            val errorResponse = t.response()?.errorBody()?.string()
                            _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                            Log.e(
                                LoginViewModel.TAG,
                                "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                            )
                        } else if (t is SocketTimeoutException) {
                            _toastText.value = "Request timeout. Please try again later."
                            Log.e(LoginViewModel.TAG, "onFailure: Request timeout", t)
                        }
                    }
                })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(LoginViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    suspend fun getUserById() {
        _isLoading.value = true
        _toastText.value = ""
        try {
            val userId = settingsPreferences.getLoginSession().first().userId
            val token = settingsPreferences.getLoginSession().first().token
            apiServiceMain.getUserById(userId, "Bearer $token")
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val userResponse = response.body()
                            if (userResponse?.error != null) {
                                _toastText.value = userResponse.error.toString()
                                Log.e(
                                    LoginViewModel.TAG,
                                    "onFailure: ${userResponse.error}"
                                )
                            } else {
                                _userResponse.value = userResponse
                                _toastText.value = "User Found"
                                Log.d(
                                    LoginViewModel.TAG,
                                    "onSuccess: User Found ${response.body()?.name.toString()}"
                                )
                            }
                        } else {
                            _toastText.value = "Find User request failed"
                            Log.e(LoginViewModel.TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        _isLoading.value = false
                        _toastText.value = t.message.toString()
                        Log.e(LoginViewModel.TAG, "onFailure: ${t.message.toString()}")
                        if (t is HttpException) {
                            val errorResponse = t.response()?.errorBody()?.string()
                            _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                            Log.e(
                                LoginViewModel.TAG,
                                "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                            )
                        } else if (t is SocketTimeoutException) {
                            _toastText.value = "Request timeout. Please try again later."
                            Log.e(LoginViewModel.TAG, "onFailure: Request timeout", t)
                        }
                    }
                })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(LoginViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    suspend fun scan(imageFile: File) {
        _isLoading.value = true
        _toastText.value = ""
        val requestImageFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val photoMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            requestImageFile
        )
        try {
            val token = settingsPreferences.getLoginSession().first().token
            val response = apiServiceMachineLearning.scan("Bearer $token", photoMultipart)
            if (response.isSuccessful) {
                val scanResponse = response.body()
                if (scanResponse?.error != null) {
                    _toastText.value = scanResponse.error.toString()
                    Log.e(CalculateViewModel.TAG, "onFailure: ${scanResponse.error}")
                } else {
                    _scanResponse.value = scanResponse
                    _toastText.value = scanResponse?.hasil.toString()
                    Log.d(CalculateViewModel.TAG, "onSuccess: ${scanResponse?.hasil}")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) {
                    response.message()
                } else {
                    try {
                        val errorJson = JSONObject(errorBody)
                        errorJson.getString("message")
                    } catch (e: JSONException) {
                        response.message()
                    }
                }
                _toastText.value = errorMessage.toString()
                Log.e(CalculateViewModel.TAG, "onFailure: $errorMessage")
            }
        } catch (e: Exception) {
            _toastText.value = e.message.toString()
            Log.e(CalculateViewModel.TAG, "onFailure: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    fun getFoodList(hasil: String) {
        _isLoading.value = true
        _toastText.value = ""
        val foodRequest = FoodRequest(hasil)
        try {
            apiServiceMain.getFoodList(foodRequest).enqueue(object : Callback<List<FoodResponse>> {
                override fun onResponse(
                    call: Call<List<FoodResponse>>,
                    response: Response<List<FoodResponse>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val foodResponse = response.body()
                        if (foodResponse != null) {
                            _foodResponse.value = foodResponse
                            _toastText.value = "Food list fetched successfully"
                            Log.d(
                                CalculateViewModel.TAG, "onSuccess: Food list fetched successfully"
                            )
                        } else {
                            _toastText.value = "Null response received"
                            Log.e(CalculateViewModel.TAG, "onFailure: Null response received")
                        }
                    } else {
                        _toastText.value = response.message()
                        Log.e(CalculateViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<FoodResponse>>, t: Throwable) {
                    _isLoading.value = false
                    _toastText.value = t.message.toString()
                    Log.e(CalculateViewModel.TAG, "onFailure: ${t.message.toString()}")
                    if (t is HttpException) {
                        val errorResponse = t.response()?.errorBody()?.string()
                        _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                        Log.e(
                            CalculateViewModel.TAG,
                            "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                        )
                    } else if (t is SocketTimeoutException) {
                        _toastText.value = "Request timeout. Please try again later."
                        Log.e(CalculateViewModel.TAG, "onFailure: Request timeout", t)
                    }
                }
            })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(CalculateViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    fun getTrackingList() {
        _isLoading.value = true
        _toastText.value = ""
        try {
            apiServiceMain.getTrackingList().enqueue(object : Callback<List<TrackingResponse>> {
                override fun onResponse(
                    call: Call<List<TrackingResponse>>,
                    response: Response<List<TrackingResponse>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val trackingResponse = response.body()
                        _trackingResponse.value = trackingResponse
                        _toastText.value = "Tracking list fetched successfully"
                        Log.d(
                            TrackingViewModel.TAG, "onSuccess: Tracking list fetched successfully"
                        )
                    } else {
                        _toastText.value = response.message()
                        Log.e(TrackingViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<TrackingResponse>>, t: Throwable) {
                    _isLoading.value = false
                    _toastText.value = t.message.toString()
                    Log.e(TrackingViewModel.TAG, "onFailure: ${t.message.toString()}")
                    if (t is HttpException) {
                        val errorResponse = t.response()?.errorBody()?.string()
                        _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                        Log.e(
                            TrackingViewModel.TAG,
                            "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                        )
                    } else if (t is SocketTimeoutException) {
                        _toastText.value = "Request timeout. Please try again later."
                        Log.e(TrackingViewModel.TAG, "onFailure: Request timeout", t)
                    }
                }
            })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(TrackingViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    fun getRecommendationList(recommendationRequest: RecommendationRequest) {
        _isLoading.value = true
        _toastText.value = ""

        try {
            apiServiceMain.getRecommendationList(recommendationRequest)
                .enqueue(object : Callback<List<RecommendationResponse>> {
                    override fun onResponse(
                        call: Call<List<RecommendationResponse>>,
                        response: Response<List<RecommendationResponse>>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val recommendationResponse = response.body()
                            _recommendationResponse.value = recommendationResponse
                            Log.d(
                                FormRecommendationViewModel.TAG,
                                "onSuccess: Recommendation list fetched successfully"
                            )
                        } else {
                            _toastText.value = response.message()
                            Log.e(FormRecommendationViewModel.TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<List<RecommendationResponse>>, t: Throwable) {
                        _isLoading.value = false
                        _toastText.value = t.message.toString()
                        Log.e(FormRecommendationViewModel.TAG, "onFailure: ${t.message.toString()}")
                        if (t is HttpException) {
                            val errorResponse = t.response()?.errorBody()?.string()
                            _toastText.value = "HTTP Error: ${errorResponse ?: t.message()}"
                            Log.e(
                                FormRecommendationViewModel.TAG,
                                "onFailure: HTTP Error: ${errorResponse ?: t.message()}"
                            )
                        } else if (t is SocketTimeoutException) {
                            _toastText.value = "Request timeout. Please try again later."
                            Log.e(FormRecommendationViewModel.TAG, "onFailure: Request timeout", t)
                        }
                    }
                })
        } catch (e: Exception) {
            _isLoading.value = false
            _toastText.value = e.message.toString()
            Log.e(FormRecommendationViewModel.TAG, "onFailure: ${e.message}")
        }
    }

    suspend fun addFoodHistory(
        jenis: String,
        name: String,
        restaurant: String,
        menu: String,
        quantity: Int,
        calorie: Float,
        createdAt: String,
        imageFile: File?
    ) {
        _isLoading.value = true
        _toastText.value = ""

        val userId = settingsPreferences.getLoginSession().first().userId
        val token = settingsPreferences.getLoginSession().first().token

        val requestImageFile = imageFile?.asRequestBody("image/*".toMediaTypeOrNull())
        val photoMultipart = MultipartBody.Part.createFormData(
            "image",
            imageFile?.name,
            requestImageFile!!
        )

        val dataJsonObject = JSONObject().apply {
            put("jenis", jenis)
            put("name", name)
            put("restaurant", restaurant)
            put("menu", menu)
            put("quantity", quantity)
            put("calorie", calorie)
            put("created_at", createdAt)
        }
        val requestBody =
            dataJsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        try {
            val response =
                apiServiceMain.addFoodHistory(userId, "Bearer $token", photoMultipart, requestBody)
            if (response.isSuccessful) {
                val foodHistoryResponse = response.body()
                if (foodHistoryResponse?.id != null) {
                    _foodHistoryResponse.value = foodHistoryResponse
                    Log.d(CalculateViewModel.TAG, "onSuccess: History activity created")
                } else {
                    _toastText.value = "Failed to create exercise history"
                    Log.e(CalculateViewModel.TAG, "onFailure: Failed to create history activity")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) {
                    response.message()
                } else {
                    try {
                        val errorJson = JSONObject(errorBody)
                        errorJson.getString("message")
                    } catch (e: JSONException) {
                        response.message()
                    }
                }
                _toastText.value = errorMessage.toString()
                Log.e(CalculateViewModel.TAG, "onFailure: Server error: $errorMessage")
            }
        } catch (e: Exception) {
            _toastText.value = e.message.toString()
            Log.e(CalculateViewModel.TAG, "onFailure: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun addExerciseHistory(
        jenis: String,
        name: String,
        durationMinutes: Int,
        calorie: Float,
        createdAt: String
    ) {
        _isLoading.value = true
        _toastText.value = ""

        val userId = settingsPreferences.getLoginSession().first().userId
        val token = settingsPreferences.getLoginSession().first().token

        val dataJsonObject = JSONObject().apply {
            put("jenis", jenis)
            put("name", name)
            put("durasi_menit", durationMinutes)
            put("calorie", calorie)
            put("created_at", createdAt)
        }
        val requestBody =
            dataJsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        try {
            val response = apiServiceMain.addExerciseHistory(userId, "Bearer $token", requestBody)
            if (response.isSuccessful) {
                val exerciseHistoryResponse = response.body()
                if (exerciseHistoryResponse?.id != null) {
                    _exerciseHistoryResponse.value = exerciseHistoryResponse
                    _toastText.value = "Exercise history created successfully"
                    Log.d(
                        TrackingViewModel.TAG,
                        "onSuccess: Exercise history created successfully for ${exerciseHistoryResponse.name}"
                    )
                } else {
                    _toastText.value = "Failed to create exercise history"
                    Log.e(TrackingViewModel.TAG, "onFailure: Failed to create exercise history")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) {
                    response.message()
                } else {
                    try {
                        val errorJson = JSONObject(errorBody)
                        errorJson.getString("message")
                    } catch (e: JSONException) {
                        response.message()
                    }
                }
                _toastText.value = errorMessage.toString()
                Log.e(TrackingViewModel.TAG, "onFailure: Server error: $errorMessage")
            }
        } catch (e: Exception) {
            _toastText.value = e.message.toString()
            Log.e(TrackingViewModel.TAG, "onFailure: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun editProfile(
        weightKg: Double,
        heightCm: Double,
        imageFile: File?
    ) {
        _isLoading.value = true
        _toastText.value = ""

        val userId = settingsPreferences.getLoginSession().first().userId
        val token = settingsPreferences.getLoginSession().first().token

        val dataJsonObject = JSONObject().apply {
            put("weight_kg", weightKg)
            put("height_cm", heightCm)
        }
        val requestBody =
            dataJsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (imageFile != null) {
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val photoMultipart = MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestImageFile
            )
            try {
                val response =
                    apiServiceMain.editProfileWithImage(userId, "Bearer $token", photoMultipart, requestBody)
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    if (messageResponse?.error != null) {
                        _toastText.value = messageResponse.error.toString()
                        Log.e(EditProfileViewModel.TAG, "onFailure: ${messageResponse.error}")
                    } else {
                        _messageResponse.value = messageResponse
                        _toastText.value = messageResponse?.message.toString()
                        Log.d(EditProfileViewModel.TAG, "onSuccess: ${messageResponse?.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        response.message()
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            response.message()
                        }
                    }
                    val serverError = "Server error: $errorMessage"
                    _toastText.value = serverError
                    Log.e(EditProfileViewModel.TAG, "onFailure: $serverError")
                    Log.e(
                        EditProfileViewModel.TAG,
                        "Server Response: ${response.code()} - $errorMessage"
                    )
                }
            } catch (e: Exception) {
                _toastText.value = e.message.toString()
                Log.e(EditProfileViewModel.TAG, "onFailure: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        } else {
            try {
                val response =
                    apiServiceMain.editProfileWithoutImage(userId, "Bearer $token", requestBody)
                if (response.isSuccessful) {
                    val messageResponse = response.body()
                    if (messageResponse?.error != null) {
                        _toastText.value = messageResponse.error.toString()
                        Log.e(EditProfileViewModel.TAG, "onFailure: ${messageResponse.error}")
                    } else {
                        _messageResponse.value = messageResponse
                        _toastText.value = messageResponse?.message.toString()
                        Log.d(EditProfileViewModel.TAG, "onSuccess: ${messageResponse?.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody.isNullOrEmpty()) {
                        response.message()
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            errorJson.getString("message")
                        } catch (e: JSONException) {
                            response.message()
                        }
                    }
                    val serverError = "Server error: $errorMessage"
                    _toastText.value = serverError
                    Log.e(EditProfileViewModel.TAG, "onFailure: $serverError")
                    Log.e(
                        EditProfileViewModel.TAG,
                        "Server Response: ${response.code()} - $errorMessage"
                    )
                }
            } catch (e: Exception) {
                _toastText.value = e.message.toString()
                Log.e(EditProfileViewModel.TAG, "onFailure: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    suspend fun getAllHistory(
        userId: String,
        token: String,
    ) {
        _isLoading.value = true
        _toastText.value = ""

        try {
            val response = apiServiceMain.getAllHistory( "Bearer $token", userId)
            if (response.isSuccessful) {
                val historyResponse = response.body()
                if (historyResponse != null) {
                    _historyResponse.value = historyResponse
                    _toastText.value = "History fetched successfully"
                    Log.d(ContentValues.TAG, "onSuccess: History fetched successfully")
                } else {
                    _toastText.value = "Failed to fetch history"
                    Log.e(ContentValues.TAG, "onFailure: Failed to create history activity")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) {
                    response.message()
                } else {
                    try {
                        val errorJson = JSONObject(errorBody)
                        errorJson.getString("message")
                    } catch (e: JSONException) {
                        response.message()
                    }
                }
                val serverError = "Server error: $errorMessage"
                _toastText.value = serverError
                Log.e(ContentValues.TAG, "onFailure: $serverError")
                Log.e(
                    ContentValues.TAG,
                    "Server Response: ${response.code()} - $errorMessage"
                )
            }
        } catch (e: Exception) {
            _toastText.value = e.message.toString()
            Log.e(ContentValues.TAG, "onFailure: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }


    fun getFirebaseAuth(): FirebaseAuth = auth

    fun getLoginSession(): LiveData<Session> {
        return settingsPreferences.getLoginSession().asLiveData()
    }

    suspend fun saveLoginSession(
        userId: String,
        userToken: String,
        user: UserResponse,
        calorieNeeds: Double
    ) {
        settingsPreferences.saveLoginSession(userId, userToken, user, calorieNeeds)
    }

    suspend fun editLoginSession(
        user: UserResponse,
        calorieNeeds: Double
    ) {
        settingsPreferences.editLoginSession(user, calorieNeeds)
    }

    suspend fun clearLoginSession() {
        settingsPreferences.clearLoginSession()
    }

    fun logout() {
        _isLoading.value = true
        auth.signOut()
        _isLoading.value = false
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            preferences: SettingsPreferences,
            apiServiceMain: ApiServiceMain,
            apiServiceMachineLearning: ApiServiceMachineLearning
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(preferences, apiServiceMain, apiServiceMachineLearning)
            }.also { instance = it }
    }
}