package com.example.nittalk.ui

import com.example.nittalk.firebase.FirebaseSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class MainActivityRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    private val currentUserId = Firebase.auth.currentUser!!.uid

    @ExperimentalCoroutinesApi
    suspend fun makeCurrentUserOnline() =
        firebaseSource.makeCurrentUserOnline(currentUserId)

    @ExperimentalCoroutinesApi
    suspend fun makeCurrentUserOffline() =
        firebaseSource.makeCurrentUserOffline(currentUserId)

}