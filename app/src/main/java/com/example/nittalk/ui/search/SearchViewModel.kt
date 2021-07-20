package com.example.nittalk.ui.search

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {

    private val currentUserId = Firebase.auth.currentUser!!.uid
    val noResultFoundVisibility = View.GONE

    fun getCurrentUser() = searchRepository.getCurrentUser(currentUserId)

    val searchQuery = MutableStateFlow("#")

    @ExperimentalCoroutinesApi
    val searchUserList = searchQuery.flatMapLatest { query ->
        searchRepository.getUserByName(query, currentUserId)
    }.asLiveData()

    @ExperimentalCoroutinesApi
    fun sendFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.sendFriendRequest(friendId, currentUserId)
    }

    @ExperimentalCoroutinesApi
    fun cancelFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.cancelFriendRequest(friendId, currentUserId)
    }

    @ExperimentalCoroutinesApi
    fun acceptFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.acceptFriendRequest(friendId, currentUserId)
    }

    @ExperimentalCoroutinesApi
    fun declineFriendRequest(friendId: String) = viewModelScope.launch {
        searchRepository.declineFriendRequest(friendId, currentUserId)
    }

}