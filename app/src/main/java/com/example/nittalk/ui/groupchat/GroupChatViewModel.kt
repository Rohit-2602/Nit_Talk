package com.example.nittalk.ui.groupchat

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.PreferencesManager
import com.example.nittalk.util.Constant.CHANNEL_SELECTED
import com.example.nittalk.util.Constant.GROUP_SELECTED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class GroupChatViewModel @ViewModelInject constructor(
    private val groupChatRepository: GroupChatRepository,
    private val preferencesManager: PreferencesManager
) :
    ViewModel() {

    private val currentUserUid = groupChatRepository.currentUser!!.uid

    @ExperimentalCoroutinesApi
    val currentUserGroups = groupChatRepository.getUserGroup(currentUserUid).asLiveData()

    fun updateGroupSelected(groupId: String) =
        viewModelScope.launch {
            preferencesManager.updateGroupSelected(GROUP_SELECTED, groupId)
        }

    private val groupSelected = preferencesManager.groupSelected

    val selectedGroupId get() = run {
        var id = ""
        viewModelScope.launch {
            id = groupSelected.first()
        }
        id
    }

    fun updateChannelSelected(channelId: String) =
        viewModelScope.launch {
            preferencesManager.updateChannelSelected(CHANNEL_SELECTED, channelId)
        }

    private val channelSelected = preferencesManager.channelSelected

    val selectedChannelId get() = run {
        var id = ""
        viewModelScope.launch {
            id = channelSelected.first()
        }
        id
    }

    val textChannels = groupSelected.flatMapLatest {
        groupChatRepository.getGroupTextChannels(it)
    }

}