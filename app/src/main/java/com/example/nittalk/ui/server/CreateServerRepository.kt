package com.example.nittalk.ui.server

import android.app.Activity
import android.net.Uri
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class CreateServerRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    suspend fun getUserById(userId: String) =
        firebaseSource.getUserById(userId)

    suspend fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        firebaseSource.uploadImage(imageUri, userId, activity)

    suspend fun imageDownloadUrl(userId: String) : String =
        firebaseSource.getProfileImageDownloadUrl(userId)

    suspend fun createNewServer(userId: String, groupName: String, groupDp: String?, activity: Activity) =
        firebaseSource.createNewServer(userId, groupName, groupDp, activity)

}