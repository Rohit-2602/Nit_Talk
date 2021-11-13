package com.example.nittalk.ui.server

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateServerViewModel @Inject constructor(
    private val serverRepository: CreateServerRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    fun createNewServer(userId: String, groupName: String, groupDp: String?, activity: Activity) = viewModelScope.launch {
        serverRepository.createNewServer(userId, groupName, groupDp, activity)
    }

    fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        viewModelScope.launch {
            serverRepository.uploadImage(imageUri, userId, activity)
        }

    fun imageDownloadUrl(userId: String): MutableLiveData<String> {
        val imageDownloadUrl = MutableLiveData<String>()
        viewModelScope.launch {
            val url = serverRepository.imageDownloadUrl(userId)
            imageDownloadUrl.postValue(url)
        }
        return imageDownloadUrl
    }

//    fun updateGroupSelected(groupId: String) =
//        viewModelScope.launch {
//            preferencesManager.updateGroupSelected(Constant.GROUP_SELECTED, groupId)
//        }

}