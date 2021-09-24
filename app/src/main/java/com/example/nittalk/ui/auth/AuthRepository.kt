package com.example.nittalk.ui.auth

import android.app.Activity
import android.net.Uri
import com.example.nittalk.data.User
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    val currentUser = firebaseSource.currentUser()
    val progress = firebaseSource.progress
    val enable = firebaseSource.enable
    val loginState = firebaseSource.loginState

    suspend fun saveUserToDB(user: User) =
        firebaseSource.saveUserToDB(user)

    fun createUser(email: String, password: String, activity: Activity) =
        firebaseSource.createUserWithEmailAndPassword(email, password, activity)

    fun signIn(email: String, password: String, loginFragment: LoginFragment) =
        firebaseSource.signInWithEmailAndPassword(email, password, loginFragment)

    suspend fun createUser(user: User, infoFragment: InfoFragment) =
        firebaseSource.createUser(user, infoFragment)

    suspend fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        firebaseSource.uploadImage(imageUri, userId, activity)

    suspend fun imageDownloadUrl(imageUri: Uri?, userId: String) : String =
        firebaseSource.getImageDownloadUrl(imageUri, userId)

    fun addUserToGroup(user: User, activity: Activity) =
        firebaseSource.addUserToGroup(user, activity)

}