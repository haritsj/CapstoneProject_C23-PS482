package com.project.capstoneapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.capstoneapp.data.Repository
import com.project.capstoneapp.di.Injection
import com.project.capstoneapp.ui.auth.login.LoginViewModel
import com.project.capstoneapp.ui.auth.register.RegisterViewModel
import com.project.capstoneapp.ui.camera.CalculateViewModel
import com.project.capstoneapp.ui.camera.ScanViewModel
import com.project.capstoneapp.ui.main.exercise.fragment.ListRecommendationViewModel
import com.project.capstoneapp.ui.main.exercise.fragment.TrackingViewModel
import com.project.capstoneapp.ui.main.exercise.fragment.recommendationfragment.FormRecommendationViewModel
import com.project.capstoneapp.ui.main.history.HistoryViewModel
import com.project.capstoneapp.ui.main.home.HomeViewModel
import com.project.capstoneapp.ui.main.profile.EditProfileViewModel
import com.project.capstoneapp.ui.main.profile.ProfileViewModel
import com.project.capstoneapp.ui.onboarding.OnboardingViewModel

class ViewModelFactory(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CalculateViewModel::class.java) -> {
                CalculateViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TrackingViewModel::class.java) -> {
                TrackingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FormRecommendationViewModel::class.java) -> {
                FormRecommendationViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ListRecommendationViewModel::class.java) -> {
                ListRecommendationViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}