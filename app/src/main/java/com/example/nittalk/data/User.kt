package com.example.nittalk.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var profileImageUrl: String = "",
    var semester: String = "",
    var branch: String = "",
    var section: String = "",
    @Ignore
    var groups: MutableList<String> = ArrayList()
)