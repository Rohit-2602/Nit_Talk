package com.example.nittalk.ui

import com.example.nittalk.firebase.FirebaseSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class MainActivityRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private val currentUserId = Firebase.auth.currentUser!!.uid

    suspend fun makeCurrentUserOnline() =
        firebaseSource.makeCurrentUserOnline(currentUserId)

    suspend fun makeCurrentUserOffline() =
        firebaseSource.makeCurrentUserOffline(currentUserId)

}