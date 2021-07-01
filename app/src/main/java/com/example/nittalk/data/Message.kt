package com.example.nittalk.data

data class Message(
    val senderId: String,
    val message: String,
    val messageId: String,
    val imageUrl: String,
    val sendAt: String
)