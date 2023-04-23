package com.rhnlf.storyapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhnlf.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: UserPreference) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.deleteUser()
        }
    }
}