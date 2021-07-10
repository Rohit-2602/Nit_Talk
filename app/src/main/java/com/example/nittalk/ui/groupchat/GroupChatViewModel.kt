package com.example.nittalk.ui.groupchat

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.PreferencesManager
import com.example.nittalk.firebase.FirebaseUtil
import com.example.nittalk.util.Constant.GROUP_SELECTED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatViewModel @ViewModelInject constructor(
    private val groupChatRepository: GroupChatRepository,
    private val preferencesManager: PreferencesManager,
    private val firebaseUtil: FirebaseUtil
) :
    ViewModel() {

    val currentUserUid = groupChatRepository.currentUser!!.uid

    val currentUserFromDB = groupChatRepository.getCurrentUserFromDB()

    @ExperimentalCoroutinesApi
    val currentUserGroups = groupChatRepository.getUserGroup(currentUserUid).asLiveData()

    private val groupPref = groupChatRepository.getGroupPref()

    private val groupSelected = preferencesManager.groupSelected

    @ExperimentalCoroutinesApi
    val currentGroup = groupSelected.flatMapLatest {
        groupChatRepository.getGroupById(it)
    }

    @ExperimentalCoroutinesApi
    val groupName = groupSelected.flatMapLatest { groupSelected ->
        groupChatRepository.getGroupName(groupSelected)
    }.asLiveData()

    val selectedGroupId
        get() = run {
            var id = ""
            viewModelScope.launch {
                id = groupSelected.first()
            }
            id
        }

    fun updateGroupSelected(groupId: String) =
        viewModelScope.launch {
            preferencesManager.updateGroupSelected(GROUP_SELECTED, groupId)
        }

    @ExperimentalCoroutinesApi
    val channelName = groupSelected.flatMapLatest { groupSelected ->
        val channelId =
            groupPref.first().find { it.groupSelectedId == groupSelected }?.channelSelectedId
                ?: groupSelected.first() + "General"
        groupChatRepository.getChannelName(groupSelected, channelId)
    }.asLiveData()

    @ExperimentalCoroutinesApi
    val channelSelected = groupPref.flatMapLatest { serverSelectedList ->
        val channelSelectedFlow = MutableStateFlow(groupSelected.first() + "General")
        channelSelectedFlow.value =
            serverSelectedList.find { it.groupSelectedId == groupSelected.first() }?.channelSelectedId
                ?: groupSelected.first() + "General"
        channelSelectedFlow
    }.asLiveData()

    fun update(groupSelectedId: String) = viewModelScope.launch(Dispatchers.IO) {
        groupChatRepository.update(groupSelectedId)
    }

    fun updateChannelSelected(groupSelectedId: String, channelSelectedId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            groupChatRepository.updateChannelSelected(groupSelectedId, channelSelectedId)
        }

    @ExperimentalCoroutinesApi
    val messages = groupPref.flatMapLatest { serverSelectedList ->
        val channelId =
            serverSelectedList.find { it.groupSelectedId == groupSelected.first() }?.channelSelectedId
                ?: groupSelected.first() + "General"
        groupChatRepository.getMessages(groupSelected.first(), channelId)
    }

    @ExperimentalCoroutinesApi
    val textChannels = groupSelected.flatMapLatest {
        groupChatRepository.getGroupTextChannels(it)
    }

    @ExperimentalCoroutinesApi
    fun sendMessage(messageText: String, imageUrl: String) =
        viewModelScope.launch {
            groupChatRepository.sendMessage(
                groupPref.first().find { it.channelSelectedId == channelSelected.asFlow().first() }!!,
                messageText, imageUrl, currentUserFromDB.first()
            )
        }

    fun sendNotification(context: Context, title: String, message: String, userId: String) =
        firebaseUtil.sendNotification(context, title, message, userId)

}