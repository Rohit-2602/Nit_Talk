package com.example.nittalk.ui.profile

import android.app.Activity
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    suspend fun signOut(activity: Activity) = firebaseSource.logout(activity)

}