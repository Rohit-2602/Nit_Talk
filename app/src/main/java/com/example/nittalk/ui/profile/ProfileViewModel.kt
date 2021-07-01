package com.example.nittalk.ui.profile

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(private val repository: ProfileRepository): ViewModel() {

    fun signOut(activity: Activity) =
        viewModelScope.launch {
            repository.signOut(activity)
        }

}