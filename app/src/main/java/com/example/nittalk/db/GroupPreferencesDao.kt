package com.example.nittalk.db

import androidx.room.*
import com.example.nittalk.data.GroupPreferences
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupPreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(groupPreferences: GroupPreferences)

    @Delete
    suspend fun removeServer(groupPreferences: GroupPreferences)

    @Query("SELECT * FROM group_pref")
    fun getSelectedGroupChannel(): Flow<List<GroupPreferences>>

    @Query("UPDATE group_pref SET channelSelectedId =:channelSelectedId WHERE groupSelectedId = :groupSelectedId")
    fun updateChannelSelected(groupSelectedId: String, channelSelectedId: String)

    @Query("UPDATE group_pref SET `update` = `update`+1 WHERE groupSelectedId = :groupSelectedId")
    fun update(groupSelectedId: String)

}