package com.example.nittalk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nittalk.data.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table WHERE id LIKE '%' || :userId || '%'")
    fun getCurrentUser(userId: String): Flow<User>

    @Query("UPDATE user_table SET backgroundImageUrl =:backgroundImage WHERE id = :userId")
    fun updateUserBackgroundImage(userId: String, backgroundImage: String)

    @Query("UPDATE user_table SET profileImageUrl =:profileImage WHERE id = :userId")
    fun updateUserProfileImage(userId: String, profileImage: String)

}