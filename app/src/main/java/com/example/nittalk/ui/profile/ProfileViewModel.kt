package com.example.nittalk.ui.profile

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository): ViewModel() {

    fun signOut(activity: Activity) =
        viewModelScope.launch {
            repository.signOut(activity)
        }

    val currentUser = repository.getCurrentUser().asLiveData()

    fun uploadImageAndGetDownloadUrl(imageUri: Uri) =
            repository.uploadImageAndGetDownloadUrl(imageUri)

    fun updateBackgroundImage(backgroundImage: String) =
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateBackgroundImage(backgroundImage)
        }

}