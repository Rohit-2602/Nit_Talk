package com.example.nittalk.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    var groupId: String = "",
    val groupName: String = "",
    val groupDp: String = "",
    val channelsId: MutableList<String> = ArrayList(),
    val voiceChannels: MutableList<String> = ArrayList(),
    val textChannels: MutableList<String> = ArrayList(),
    val members: MutableList<String> = ArrayList()
): Parcelable