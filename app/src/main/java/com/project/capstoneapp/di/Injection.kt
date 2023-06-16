package com.project.capstoneapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.data.datastore.SettingsPreferences
import com.project.capstoneapp.data.remote.request.api.ApiConfig

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")

object Injection {
    fun provideRepository(context: Context): Repository {
        val preferences = SettingsPreferences.getInstance(context.dataStore)
        val apiServiceMain = ApiConfig.getApiServiceMain()
        val apiServiceMachineLearning = ApiConfig.getApiServiceMachineLearning()
        return Repository.getInstance(preferences, apiServiceMain, apiServiceMachineLearning)
    }
}