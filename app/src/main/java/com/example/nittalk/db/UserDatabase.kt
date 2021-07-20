package com.example.nittalk.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nittalk.data.GroupPreferences
import com.example.nittalk.data.User

@Database(entities = [User::class, GroupPreferences::class], exportSchema = false, version = 2)
abstract class UserDatabase: RoomDatabase() {

    abstract fun getUserDao() : UserDao
    abstract fun getServerDao() : GroupPreferencesDao

}