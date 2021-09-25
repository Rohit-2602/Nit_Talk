package com.example.nittalk.ui.profile

import android.app.Activity
import android.net.Uri
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private val currentUserUid = firebaseSource.currentUser()!!.uid

    suspend fun signOut(activity: Activity) = firebaseSource.logout(activity)

    fun getCurrentUser() = firebaseSource.getCurrentUserFromDB(currentUserUid)

    fun uploadImageAndGetDownloadUrl(imageUri: Uri) =
        firebaseSource.uploadImageAndGetDownloadUrl(imageUri, currentUserUid)

    suspend fun updateBackgroundImage(backgroundImage: String) =
        firebaseSource.updateUserBackgroundImage(currentUserUid, backgroundImage)

}