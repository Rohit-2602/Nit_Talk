package com.example.nittalk.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {

    private val currentUserId = Firebase.auth.currentUser!!.uid

    fun getUserById(userId: String) = searchRepository.getUserById(userId)

    val currentUser = searchRepository.getCurrentUser(currentUserId)

    val searchQuery = MutableStateFlow("#")

    val searchUserList = searchQuery.flatMapLatest { query ->
        searchRepository.getUserByName(query, currentUserId)
    }.asLiveData()

    
//    val incomingRequests = currentUser.flatMapLatest { currentUser ->
//        searchRepository.getIncomingRequests(currentUserId)
//    }.asLiveData()

    val incomingRequests = searchRepository.getIncomingRequests(currentUserId).asLiveData()
    
    val outgoingRequests = searchRepository.getOutgoingRequests(currentUserId).asLiveData()

    val userFriends = searchRepository.getUserFriends(currentUserId).asLiveData()

//    val onlineFriends = searchRepository.getOnlineFriends(currentUserId).asLiveData()
//
//
//    val offlineFriends = searchRepository.getOfflineFriends(currentUserId).asLiveData()

    
    fun sendFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.sendFriendRequest(friendId, currentUserId)
    }

    
    fun cancelFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.cancelFriendRequest(friendId, currentUserId)
    }

    
    fun acceptFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.acceptFriendRequest(friendId, currentUserId)
    }

    
    fun declineFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.declineFriendRequest(friendId, currentUserId)
    }

}