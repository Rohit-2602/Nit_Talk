package com.example.nittalk.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendChatViewModel @Inject constructor(private val friendChatRepository: FriendChatRepository) :
    ViewModel() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
//    suspend fun getCurrentUser() = getUserById(currentUserId)

    val currentUser = friendChatRepository.getUserFlow(currentUserId)

    suspend fun getUserById(userId: String) =
        friendChatRepository.getUserById(userId)

    val userInbox = friendChatRepository.getUserInbox(currentUserId).asLiveData()

    fun getFriendMessages(friendId: String) =
        friendChatRepository.getFriendMessages(currentUserId, friendId).asLiveData()

    fun sendPersonalMessage(friendId: String, imageUrl: String, messageText: String) =
        viewModelScope.launch {
            val currentUser = currentUser.first()
            friendChatRepository.sendPersonalMessage(
                currentUser = currentUser,
                friendUserId = friendId,
                messageText = messageText,
                imageUrl = imageUrl
            )
        }


}