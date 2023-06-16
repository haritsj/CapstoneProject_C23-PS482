package com.project.capstoneapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.project.capstoneapp.data.model.Session
import com.project.capstoneapp.data.remote.response.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences private constructor(private val settingsDataStore: DataStore<Preferences>) {
    fun getLoginSession(): Flow<Session> {
        return settingsDataStore.data.map { preferences ->
            Session(
                preferences[USER_ID] ?: "",
                preferences[USER_TOKEN] ?: "",
                preferences[IS_LOGIN] ?: false,
                UserResponse(
                    error = preferences[ERROR],
                    name = preferences[USER_NAME],
                    email = preferences[USER_EMAIL],
                    password = preferences[USER_PASSWORD],
                    gender = preferences[USER_GENDER],
                    weightKg = preferences[USER_WEIGHT_KG],
                    heightCm = preferences[USER_HEIGHT_CM],
                    birthDate = preferences[USER_BIRTH_DATE],
                    photoUrl = preferences[USER_PHOTO_URL]
                ),
                preferences[USER_CALORIE_NEEDS] ?: 0.0,
            )
        }
    }

    suspend fun saveLoginSession(userId: String, userToken: String, user: UserResponse, calorieNeeds: Double) {
        settingsDataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_TOKEN] = userToken
            preferences[IS_LOGIN] = true
            preferences[ERROR] = user.error.toString()
            preferences[USER_NAME] = user.name.toString()
            preferences[USER_EMAIL] = user.email.toString()
            preferences[USER_PASSWORD] = user.password.toString()
            preferences[USER_GENDER] = user.gender.toString()
            preferences[USER_WEIGHT_KG] =
                if (user.weightKg != null) user.weightKg.toDouble() else 0.0
            preferences[USER_HEIGHT_CM] =
                if (user.heightCm != null) user.heightCm.toDouble() else 0.0
            preferences[USER_BIRTH_DATE] = user.birthDate.toString()
            preferences[USER_PHOTO_URL] = user.photoUrl.toString()
            preferences[USER_CALORIE_NEEDS] = calorieNeeds
        }
    }

    suspend fun editLoginSession(user: UserResponse, calorieNeeds: Double) {
        settingsDataStore.edit { preferences ->
            preferences[USER_WEIGHT_KG] =
                if (user.weightKg != null) user.weightKg.toDouble() else 0.0
            preferences[USER_HEIGHT_CM] =
                if (user.heightCm != null) user.heightCm.toDouble() else 0.0
            preferences[USER_PHOTO_URL] = user.photoUrl.toString()
            preferences[USER_CALORIE_NEEDS] = calorieNeeds
        }
    }

    suspend fun clearLoginSession() {
        settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsPreferences? = null
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_TOKEN = stringPreferencesKey("user_token")
        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val ERROR = stringPreferencesKey("error")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_PASSWORD = stringPreferencesKey("user_password")
        private val USER_GENDER = stringPreferencesKey("user_gender")
        private val USER_WEIGHT_KG = doublePreferencesKey("user_weight_kg")
        private val USER_HEIGHT_CM = doublePreferencesKey("user_height_cm")
        private val USER_BIRTH_DATE = stringPreferencesKey("user_birth_date")
        private val USER_PHOTO_URL = stringPreferencesKey("user_photo_url")
        private val USER_CALORIE_NEEDS = doublePreferencesKey("user_calorie_needs")

        fun getInstance(dataStore: DataStore<Preferences>): SettingsPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}