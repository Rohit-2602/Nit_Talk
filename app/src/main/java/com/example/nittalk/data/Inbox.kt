package com.example.nittalk.data

data class Inbox(
    var friendId: String = "",
    val friendName: String = "",
    var lastMessage: String = "",
    var lastMessageTime: Long = 0L
)