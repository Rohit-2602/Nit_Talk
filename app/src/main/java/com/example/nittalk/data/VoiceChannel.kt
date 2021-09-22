package com.example.nittalk.data

data class VoiceChannel(
    val channelId: String = "",
    val channelName: String = "",
    val createdAt: Long = 0L,
    val members: MutableList<User> = ArrayList(),
    val groupId: String = ""
)