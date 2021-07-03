package com.example.nittalk.data

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.example.nittalk.util.Constant
import com.example.nittalk.util.Constant.USER_PREFERENCES
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class GroupPreferences(var groupSelectedId: String, var channelSelectedId: String)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore(USER_PREFERENCES)

    suspend fun updateLoginState(loginStateKey: String, loginState: Boolean) {
        val dataStoreKey = preferencesKey<Boolean>(loginStateKey)
        dataStore.edit { pref ->
            pref[dataStoreKey] = loginState
        }
    }

    val loginStateFlow = dataStore.data
        .map { preferences ->
            val loginStateKey = preferencesKey<Boolean>(Constant.LOGIN_STATE_KEY)
            preferences[loginStateKey] ?: false
        }

    suspend fun updateGroupSelected(groupKey: String, groupId: String) {
        val dataStoreKey = preferencesKey<String>(groupKey)
        dataStore.edit { pref ->
            pref[dataStoreKey] = groupId
        }
    }

    val groupSelected = dataStore.data
        .map { preferences ->
            val groupKey = preferencesKey<String>(Constant.GROUP_SELECTED)
            preferences[groupKey] ?: "demoGroup"
        }

    suspend fun updateChannelSelected(channelKey: String, channelId: String) {
        val dataStoreKey = preferencesKey<String>(channelKey)
        dataStore.edit { pref ->
            pref[dataStoreKey] = channelId
        }
    }

    val channelSelected = dataStore.data
        .map { preferences ->
            val channelKey = preferencesKey<String>(Constant.CHANNEL_SELECTED)
            preferences[channelKey] ?: ""
        }

    val groupPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            else {
                throw exception
            }
        }
        .map { preferences ->
            val groupKey = preferencesKey<String>(Constant.GROUP_SELECTED)
            val groupID = preferences[groupKey] ?: "demoGroup"

            val channelKey = preferencesKey<String>(Constant.CHANNEL_SELECTED)
            val channelID = preferences[channelKey] ?: "demoChannel"

            GroupPreferences(groupSelectedId = groupID, channelSelectedId = channelID)
        }

}