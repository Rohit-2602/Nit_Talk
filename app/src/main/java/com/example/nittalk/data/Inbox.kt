package com.example.nittalk.data

data class Inbox(
    var friendId: String = "",
    val friendName: String = "",
    val friendLowerCaseName: String = "",
    val friendDp: String = "",
    var lastMessage: String? = null,
    var lastMessageTime: Long? = null
)