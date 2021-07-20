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
    suspend fun getUserGroup(userId: String) =
        firebaseSource.getUserGroup(userId)

    @ExperimentalCoroutinesApi
    fun getGroupOnlineMembers() = firebaseSource.onlineGroupMembers()

    @ExperimentalCoroutinesApi
    fun getGroupOfflineMembers() = firebaseSource.offlineGroupMembers()

    @ExperimentalCoroutinesApi
    fun getGroupById(groupId: String) =
        firebaseSource.getGroupById(groupId)

    fun getGroupPref() = firebaseSource.getGroupPref()

    @ExperimentalCoroutinesApi
    fun getGroupTextChannels(groupId: String) =
        firebaseSource.getGroupTextChannels(groupId)

    @ExperimentalCoroutinesApi
    fun getChannelName(groupId: String, channelId: String) =
        firebaseSource.getChannelName(groupId, channelId)

    @ExperimentalCoroutinesApi
    fun getGroupName(groupId: String) =
        firebaseSource.getGroupName(groupId)

    fun updateChannelSelected(groupId: String, channelId: String) =
        firebaseSource.updateChannelSelected(groupId, channelId)

    fun sendMessage(groupPreferences: GroupPreferences, messageText: String, imageUrl: String, currentUser: User) =
        firebaseSource.sendMessage(groupPreferences.groupSelectedId, groupPreferences.channelSelectedId, messageText, imageUrl, currentUser)

    @ExperimentalCoroutinesApi
    fun getMessages(groupId: String, channelId: String) =
        firebaseSource.getChannelMessages(groupId, channelId)

    fun update(groupSelectedId: String) = firebaseSource.update(groupSelectedId)

}