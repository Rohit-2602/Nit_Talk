package com.example.nittalk.data

data class User(
    val id: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val semester: String = "",
    val branch: String = "",
    val section: String = "",
    val groups: MutableList<String> = ArrayList()
)