package com.example.nittalk.data

data class Message(
    val senderId: String = "",
    val message: String = "",
    var messageId: String = "",
    val imageUrl: String = "",
    val senderDp: String  = "",
    val senderName: String = "",
    val sendAt: Long = 0L
)