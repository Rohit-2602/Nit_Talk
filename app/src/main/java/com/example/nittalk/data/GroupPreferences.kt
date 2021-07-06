package com.example.nittalk.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_pref")
data class GroupPreferences(
    @PrimaryKey
    var groupSelectedId: String = "",
    var channelSelectedId: String = "",
    /* Little Hack to update pref when channel Selected
    * Bcoz in GroupChatViewModel, channelSelected observe
    * only changes in channelSelected,
    * So in this way we can update whole pref and channelSelected Trigger again :)*/
    var update: Long = 0
)