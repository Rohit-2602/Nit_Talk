package com.example.nittalk.ui.inbox

import com.example.nittalk.data.User
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class FriendChatRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    suspend fun getUserById(userId: String) =
        firebaseSource.getUserById(userId)

    fun getUserFlow(userId: String) =
        firebaseSource.getUserFlowById(userId)

    fun getUserInbox(currentUserId: String) =
        firebaseSource.getUserInbox(currentUserId)

    fun sendPersonalMessage(currentUser: User, friendUserId: String, imageUrl: String, messageText: String) =
        firebaseSource.sendPersonalMessage(currentUser = currentUser, friendId = friendUserId, messageText = messageText, imageUrl = imageUrl)

    fun getFriendMessages(currentUserId: String, friendUserId: String) =
        firebaseSource.getFriendMessages(currentUserId, friendUserId)

}