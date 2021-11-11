package com.example.nittalk.ui.inbox

import com.example.nittalk.data.Message
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

    fun sendPersonalMessage(currentUser: User, friendUserId: String, imageUrl: String, messageText: String, repliedTo: Message?) =
        firebaseSource.sendPersonalMessage(currentUser = currentUser, friendId = friendUserId, messageText = messageText, imageUrl = imageUrl, repliedTo = repliedTo)

    fun deletePersonalMessage(currentUserId: String, friendId: String, message: Message, lastMessage: Message, nextLastMessage: Message?) =
        firebaseSource.deletePersonalMessage(currentUserId, friendId, message, lastMessage, nextLastMessage)

    fun editPersonalMessage(currentUserId: String, friendId: String, message: Message, newMessage: String, lastMessage: Message) =
        firebaseSource.editPersonalMessage(currentUserId, friendId, message, newMessage, lastMessage)

    fun getFriendMessages(currentUserId: String, friendUserId: String) =
        firebaseSource.getFriendMessages(currentUserId, friendUserId)

}