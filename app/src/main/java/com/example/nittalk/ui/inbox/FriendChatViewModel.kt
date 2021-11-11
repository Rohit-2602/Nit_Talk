package com.example.nittalk.ui.inbox

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.Message
import com.example.nittalk.firebase.FirebaseUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendChatViewModel @Inject constructor(private val friendChatRepository: FriendChatRepository, private  val firebaseUtil: FirebaseUtil) :
    ViewModel() {

    val currentUserId = Firebase.auth.currentUser!!.uid
//    suspend fun getCurrentUser() = getUserById(currentUserId)

    val currentUser = friendChatRepository.getUserFlow(currentUserId)

    suspend fun getUserById(userId: String) =
        friendChatRepository.getUserById(userId)

    val userInbox = friendChatRepository.getUserInbox(currentUserId).asLiveData()

    fun getFriendMessages(friendId: String) =
        friendChatRepository.getFriendMessages(currentUserId, friendId).asLiveData()

    fun sendPersonalMessage(friendId: String, imageUrl: String, messageText: String, repliedTo: Message?) =
        viewModelScope.launch {
            val currentUser = currentUser.first()
            friendChatRepository.sendPersonalMessage(
                currentUser = currentUser,
                friendUserId = friendId,
                messageText = messageText,
                imageUrl = imageUrl,
                repliedTo = repliedTo
            )
        }

    fun editMessage(friendId: String, message: Message, messageText: String, lastMessage: Message) =
        viewModelScope.launch {
            friendChatRepository.editPersonalMessage(
                currentUserId, friendId, message, messageText, lastMessage
            )
        }

    fun deleteMessage(friendId: String, message: Message, lastMessage: Message, nextMessage: Message?) =
        viewModelScope.launch {
            friendChatRepository.deletePersonalMessage(
                currentUserId, friendId, message, lastMessage, nextMessage
            )
        }

    private val currentUserToken =
        firebaseUtil.getCurrentUserToken(currentUserId)

    fun sendNotification(context: Context, title: String, message: String, userId: String) =
        firebaseUtil.sendNotification(context, title, message, userId, currentUserToken)

}