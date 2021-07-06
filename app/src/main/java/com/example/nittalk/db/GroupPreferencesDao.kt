package com.example.nittalk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nittalk.data.GroupPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupPreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(groupPreferences: GroupPreferences)

    @Query("SELECT * FROM group_pref")
    fun getSelectedGroupChannel(): Flow<List<GroupPreferences>>

    @Query("UPDATE group_pref SET channelSelectedId =:channelSelectedId WHERE groupSelectedId = :groupSelectedId")
    fun updateChannelSelected(groupSelectedId: String, channelSelectedId: String)

    @Query("UPDATE group_pref SET `update` = `update`+1 WHERE groupSelectedId = :groupSelectedId")
    fun update(groupSelectedId: String)

}