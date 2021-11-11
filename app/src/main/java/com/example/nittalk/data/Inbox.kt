package com.example.nittalk.data

data class Inbox(
    var friendId: String = "",
    val friendName: String = "",
    var lastMessage: String? = null,
    var lastMessageTime: Long? = null
)