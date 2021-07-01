package com.example.nittalk.ui.groupchat

import com.example.nittalk.firebase.FirebaseSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class GroupChatRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    val currentUser = firebaseSource.currentUser()

    @ExperimentalCoroutinesApi
    fun getUserById(userId: String) =
        firebaseSource.getUserById(userId)

    @ExperimentalCoroutinesApi
    fun getUserGroup(userId: String) =
        firebaseSource.getUserGroup(userId)

    @ExperimentalCoroutinesApi
    fun getGroupTextChannels(groupId: String) =
        firebaseSource.getGroupTextChannels(groupId)

}