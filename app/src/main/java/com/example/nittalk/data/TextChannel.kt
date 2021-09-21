package com.example.nittalk.data

data class TextChannel(
    val channelId: String = "",
    val channelName: String = "",
    val createdAt: Long = 0L,
    val messages: MutableList<String> = ArrayList(),
    val groupId: String = ""
)