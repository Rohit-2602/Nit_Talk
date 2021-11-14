package com.example.nittalk.ui.invite

import com.example.nittalk.data.Group
import com.example.nittalk.data.Message
import com.example.nittalk.data.User
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class InviteMemberRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    fun getUserInbox(currentUserId: String) =
        firebaseSource.getUserInbox(currentUserId)

    fun sendPersonalMessage(currentUser: User, friendUserId: String, imageUrl: String, messageText: String, repliedTo: Message?, joinGroup: Group?) =
        firebaseSource.sendPersonalMessage(currentUser = currentUser, friendId = friendUserId, messageText = messageText, imageUrl = imageUrl, repliedTo = repliedTo, joinGroup = joinGroup)

}