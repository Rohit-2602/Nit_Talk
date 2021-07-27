package com.example.nittalk.ui.search

import com.example.nittalk.firebase.FirebaseSource
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    suspend fun getUserById(userId: String) = firebaseSource.getUserById(userId)

    fun getUserByName(query: String) =
        firebaseSource.searchUserByName(query)

    suspend fun getCurrentUser(currentUserId: String) =
        firebaseSource.getUserById(currentUserId)

    fun getIncomingRequests(currentUserId: String) =
        firebaseSource.getIncomingRequests(currentUserId)

    fun getOutgoingRequests(currentUserId: String) =
        firebaseSource.getOutGoingRequests(currentUserId)

    fun getOnlineFriends(currentUserId: String) =
        firebaseSource.getUserOnlineFriends(currentUserId)

    fun getOfflineFriends(currentUserId: String) =
        firebaseSource.getUserOfflineFriends(currentUserId)

    suspend fun sendFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.sendFriendRequest(friendId, currentUserId)

    suspend fun cancelFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.cancelFriendRequest(friendId, currentUserId)

    suspend fun acceptFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.acceptFriendRequest(friendId, currentUserId)

    suspend fun declineFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.declineFriendRequest(friendId, currentUserId)

}