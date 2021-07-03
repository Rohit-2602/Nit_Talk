package com.example.nittalk.ui.auth

import android.app.Activity
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.User
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(private val authRepository: AuthRepository)
    : ViewModel() {

    val currentUser = authRepository.currentUser
    val progress = authRepository.progress
    val enable = authRepository.enable

    fun saveUserToDB(user: User) =
        viewModelScope.launch {
            authRepository.saveUserToDB(user)
        }

    fun createUser(email: String, password: String, activity: Activity) =
        viewModelScope.launch {
            authRepository.createUser(email, password, activity)
        }

    fun signIn(email: String, password: String, loginFragment: LoginFragment) =
        viewModelScope.launch {
            authRepository.signIn(email, password, loginFragment)
        }

    fun createUser(user: User, infoFragment: InfoFragment) =
        viewModelScope.launch {
            authRepository.createUser(user, infoFragment)
        }

    fun addUserToGroup(user: User, activity: Activity) =
        viewModelScope.launch {
            authRepository.addUserToGroup(user, activity)
        }

    fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        viewModelScope.launch {
            authRepository.uploadImage(imageUri, userId, activity)
        }

    fun imageDownloadUrl(imageUri: Uri?, userId: String): MutableLiveData<String> {
        val imageUrl = MutableLiveData<String>()
        viewModelScope.launch {
            val url = authRepository.imageDownloadUrl(imageUri, userId)
            imageUrl.postValue(url)
        }
        return imageUrl
    }

    val loginState = authRepository.loginState.asLiveData()

//    val loginState get() = run {
//        val state = MutableLiveData(false)
//        viewModelScope.launch {
//            state.postValue(authRepository.loginState())
//        }
//        state
//    }

//    fun logOut() = authRepository.logout()

}