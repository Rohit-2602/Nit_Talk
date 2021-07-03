package com.example.nittalk.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nittalk.data.User

@Database(entities = [User::class], exportSchema = false, version = 1)
abstract class UserDatabase: RoomDatabase() {

    abstract fun getUserDao() : UserDao

}