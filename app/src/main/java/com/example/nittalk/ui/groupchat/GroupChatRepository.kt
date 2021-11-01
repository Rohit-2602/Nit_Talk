package com.example.nittalk.ui.groupchat

import com.example.nittalk.data.GroupPreferences
import com.example.nittalk.data.Message
import com.example.nittalk.data.User
import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class GroupChatRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    val currentUser = firebaseSource.currentUser()

    suspend fun getUserById(userId: String) =
        firebaseSource.getUserById(userId)

    fun getCurrentUserFromDB() =
        firebaseSource.getCurrentUserFromDB(currentUser!!.uid)

    suspend fun getUserGroup(userId: String) =
        firebaseSource.getUserGroup(userId)

    fun getGroupOnlineMembers() = firebaseSource.onlineGroupMembers()

    fun getGroupOfflineMembers() = firebaseSource.offlineGroupMembers()

    fun getGroupById(groupId: String) =
        firebaseSource.getGroupById(groupId)

    fun getGroupPref() = firebaseSource.getGroupPref()

    fun getGroupTextChannels(groupId: String) =
        firebaseSource.getGroupTextChannels(groupId)

    fun getGroupVoiceChannels(groupId: String) =
        firebaseSource.getGroupVoiceChannels(groupId)

    fun getChannelName(groupId: String, channelId: String) =
        firebaseSource.getChannelName(groupId, channelId)

    fun getGroupName(groupId: String) =
        firebaseSource.getGroupName(groupId)

    fun updateChannelSelected(groupId: String, channelId: String) =
        firebaseSource.updateChannelSelected(groupId, channelId)

    fun sendMessage(groupPreferences: GroupPreferences, messageText: String, imageUrl: String, currentUser: User) =
        firebaseSource.sendMessage(groupPreferences.groupSelectedId, groupPreferences.channelSelectedId, messageText, imageUrl, currentUser)

    fun editMessage(groupPreferences: GroupPreferences, messageText: String, message: Message) =
        firebaseSource.editMessage(groupPreferences.groupSelectedId, groupPreferences.channelSelectedId, messageText, message)

    fun deleteMessage(groupPreferences: GroupPreferences, message: Message) =
        firebaseSource.deleteMessage(groupPreferences.groupSelectedId, groupPreferences.channelSelectedId, message)

    fun getMessages(groupId: String, channelId: String) =
        firebaseSource.getChannelMessages(groupId, channelId)

    fun update(groupSelectedId: String) = firebaseSource.update(groupSelectedId)

}