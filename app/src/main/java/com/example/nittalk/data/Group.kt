package com.example.nittalk.data

data class Group(
    var groupId: String = "",
    val groupName: String = "",
    val groupDp: String = "",
    val channelsId: MutableList<String> = ArrayList(),
    val voiceChannels: MutableList<String> = ArrayList(),
    val textChannels: MutableList<String> = ArrayList(),
    val members: MutableList<String> = ArrayList()
)