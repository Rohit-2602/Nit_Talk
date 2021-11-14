package com.example.nittalk.ui.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.Group
import com.example.nittalk.data.Message
import com.example.nittalk.data.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteMemberViewModel @Inject constructor(private val inviteMemberRepository: InviteMemberRepository): ViewModel() {

    val currentUserId = Firebase.auth.currentUser!!.uid

    private val userInbox = inviteMemberRepository.getUserInbox(currentUserId)

    val searchQuery = MutableStateFlow("")

    val searchUserList = searchQuery.flatMapLatest { query ->
        userInbox.map { it.filter { inbox -> inbox.friendLowerCaseName.startsWith(query.lowercase()) } }
    }

    fun sendPersonalMessage(currentUser: User, friendId: String, imageUrl: String, messageText: String, repliedTo: Message?, joinGroup: Group?) =
        viewModelScope.launch {
            inviteMemberRepository.sendPersonalMessage(
                currentUser = currentUser,
                friendUserId = friendId,
                messageText = messageText,
                imageUrl = imageUrl,
                repliedTo = repliedTo,
                joinGroup = joinGroup
            )
        }

}