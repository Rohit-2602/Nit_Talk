package com.example.nittalk.ui.groupchat

import com.example.nittalk.data.GroupPreferences
import com.example.nittalk.data.User
import com.example.nittalk.firebase.FirebaseSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class GroupChatRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    val currentUser = firebaseSource.currentUser()

    @ExperimentalCoroutinesApi
    fun getUserById(userId: String) =
        firebaseSource.getUserById(userId)

    fun getCurrentUserFromDB() =
        firebaseSource.getCurrentUserFromDB(currentUser!!.uid)

    @ExperimentalCoroutinesApi
    fun getUserGroup(userId: String) =
        firebaseSource.getUserGroup(userId)

    @ExperimentalCoroutinesApi
    fun getGroupTextChannels(groupId: String) =
        firebaseSource.getGroupTextChannels(groupId)

    fun sendMessage(groupPreferences: GroupPreferences, messageText: String, imageUrl: String, currentUser: User) =
        firebaseSource.sendMessage(groupPreferences, messageText, imageUrl, currentUser)

    fun getMessages(groupPreferences: GroupPreferences) =
        firebaseSource.getChannelMessages(groupPreferences)

}