package com.example.nittalk.util

import com.example.nittalk.BuildConfig

object Constant {

    const val USER_PREFERENCES = "user_preferences"
    const val LOGIN_STATE_KEY = "login_state"
    const val GROUP_SELECTED = "group_selected"
    const val FIREBASE_SERVER_KEY = BuildConfig.FIREBASE_SERVER_KEY
    const val SENDER_ID = BuildConfig.FIREBASE_SENDER_ID
    const val DEFAULT_USER_DP = "https://firebasestorage.googleapis.com/v0/b/whatsapp-clone-bcfa9.appspot.com/o/abstract-user-flat-4.png?alt=media&token=0b642336-9ab7-462f-9538-963de5e616a6"

    val branchIdHashMap : HashMap<String, String> = hashMapOf(
        "Civil" to "Civil",
        "Computer Science" to "ComputerScience",
        "Electrical" to "Electrical",
        "Electronics" to "Electronics",
        "Information Technology" to "InformationTechnology",
        "Mechanical" to "Mechanical",
        "Production" to "Production"
    )

    val semesterIdHashMap : HashMap<String, String> = hashMapOf(
        "Semester 1" to "Semester1",
        "Semester 2" to "Semester2",
        "Semester 3" to "Semester3",
        "Semester 4" to "Semester4",
        "Semester 5" to "Semester5",
        "Semester 6" to "Semester6",
        "Semester 7" to "Semester7",
        "Semester 8" to "Semester8"
    )

}