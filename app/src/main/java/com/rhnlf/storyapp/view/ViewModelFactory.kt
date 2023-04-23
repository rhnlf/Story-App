package com.rhnlf.storyapp.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rhnlf.storyapp.data.local.UserPreference

class ViewModelFactory(
    private val pref: UserPreference, private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(pref) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref, application) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(application) as T
            }

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, application) as T
            }

            modelClass.isAssignableFrom(PostStoryViewModel::class.java) -> {
                PostStoryViewModel(pref, application) as T
            }

            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}