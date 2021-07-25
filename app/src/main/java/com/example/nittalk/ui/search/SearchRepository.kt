package com.example.nittalk.ui.search

import com.example.nittalk.firebase.FirebaseSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class SearchRepository @Inject constructor(private val firebaseSource: FirebaseSource) {

    @ExperimentalCoroutinesApi
    fun getUserById(userId: String) = firebaseSource.getUserById(userId)

    @ExperimentalCoroutinesApi
    fun getUserByName(query: String, currentUserId: String) =
        firebaseSource.searchUserByName(query, currentUserId)

    @ExperimentalCoroutinesApi
    fun getCurrentUser(currentUserId: String) =
        firebaseSource.getUserById(currentUserId)

//    @ExperimentalCoroutinesApi
//    fun getIncomingRequests(currentUserId: String, currentUser: User) =
//        firebaseSource.getIncomingRequests(currentUserId, currentUser)

    @ExperimentalCoroutinesApi
    fun getIncomingRequests(currentUserId: String) =
        firebaseSource.getIncomingRequests(currentUserId)

    @ExperimentalCoroutinesApi
    fun getOutgoingRequests(currentUserId: String) =
        firebaseSource.getOutGoingRequests(currentUserId)

    @ExperimentalCoroutinesApi
    fun getUserFriends(currentUserId: String) =
        firebaseSource.getUserFriends(currentUserId)

//    @ExperimentalCoroutinesApi
//    fun getOnlineFriends(currentUserId: String) =
//        firebaseSource.getOnlineFriends(currentUserId)
//
//    @ExperimentalCoroutinesApi
//    fun getOfflineFriends(currentUserId: String) =
//        firebaseSource.getOfflineFriends(currentUserId)

    @ExperimentalCoroutinesApi
    suspend fun sendFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.sendFriendRequest(friendId, currentUserId)

    @ExperimentalCoroutinesApi
    suspend fun cancelFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.cancelFriendRequest(friendId, currentUserId)

    @ExperimentalCoroutinesApi
    suspend fun acceptFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.acceptFriendRequest(friendId, currentUserId)

    @ExperimentalCoroutinesApi
    suspend fun declineFriendRequest(friendId: String, currentUserId: String) =
        firebaseSource.declineFriendRequest(friendId, currentUserId)

}