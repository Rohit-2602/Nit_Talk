package com.example.nittalk.ui.editprofile

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nittalk.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val repository: EditProfileRepository): ViewModel() {

    val currentUser = repository.currentUser
    val progress = repository.progress
    val enable = repository.enable

    private fun saveUserToDB(user: User) =
        viewModelScope.launch {
            repository.saveUserToDB(user)
        }

    fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        viewModelScope.launch {
            repository.uploadImage(imageUri, userId, activity)
        }

    fun imageDownloadUrl(imageUri: Uri?): MutableLiveData<String> {
        val imageUrl = MutableLiveData<String>()
        viewModelScope.launch {
            val url = repository.imageDownloadUrl(imageUri, currentUser!!.uid)
            imageUrl.postValue(url)
        }
        return imageUrl
    }

    private fun updateFirebaseUser(user: User) = CoroutineScope(Dispatchers.Main).launch {
        repository.updateFirebaseUser(user)
    }

    fun updateUser(user: User) {
        saveUserToDB(user)
        updateFirebaseUser(user)
    }

    fun showAlertDialog(oldUser: User, activity: Activity, context: Context, updateUser: () -> User) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Do you want to change Your College Info ?")
            .setMessage("Changing This info can cause some problem, Are you sure you want to change this information?")
            .setPositiveButton("Yes") { _, _ ->
                val newUser = updateUser()
                changeUserGroup(oldUser = oldUser, updatedUser = newUser, activity = activity)
            }
            .setNegativeButton("No") { _, _ ->

            }
            .create()
        alertDialog.show()
    }

    fun isBranchSemesterOrSectionChanged(user: User, branch: String, semester: String, section: String): Boolean {
        return (branch != user.branch || section != user.section || semester != user.semester)
    }

    private fun changeUserGroup(oldUser: User, updatedUser: User, activity: Activity) =
        CoroutineScope(Dispatchers.Main).launch {
            repository.changeUserGroup(oldUser, updatedUser, activity)
        }

}