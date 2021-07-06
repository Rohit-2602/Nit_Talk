package com.example.nittalk.data

data class Channel(
    val channelId: String = "",
    val channelName: String = "",
    val createdAt: Long = 0L,
    val messages: MutableList<String> = ArrayList(),
    val groupId: String = ""
)