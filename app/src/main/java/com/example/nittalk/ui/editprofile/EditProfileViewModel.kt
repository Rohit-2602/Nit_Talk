package com.example.nittalk.ui.editprofile

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AlertDialog
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

    suspend fun saveUserToDB(user: User) = repository.saveUserToDB(user)

    fun uploadImage(imageUri: Uri, userId: String, activity: Activity) =
        viewModelScope.launch {
            repository.uploadImage(imageUri, userId, activity)
        }

    suspend fun imageDownloadUrl(): String {
        return repository.imageDownloadUrl(currentUser!!.uid)
    }

    fun updateFirebaseUser(user: User) =
        CoroutineScope(Dispatchers.IO).launch {
            repository.updateFirebaseUser(user)
        }

    fun showAlertDialog(oldUserBranch: String, oldUserSemester: String, activity: Activity, context: Context, updateUser: () -> User) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Do you want to change Your College Info ?")
            .setMessage("Changing This info can cause some problem, Are you sure you want to change this information?")
            .setPositiveButton("Yes") { _, _ ->
                val newUser = updateUser()
                changeUserGroup(oldUserBranch = oldUserBranch, oldUserSemester = oldUserSemester, updatedUser = newUser, activity = activity)
            }
            .setNegativeButton("No") { _, _ ->

            }
            .create()
        alertDialog.show()
    }

    fun isBranchSemesterOrSectionChanged(user: User, branch: String, semester: String, section: String): Boolean {
        return (branch != user.branch || section != user.section || semester != user.semester)
    }

    private fun changeUserGroup(oldUserBranch: String, oldUserSemester: String, updatedUser: User, activity: Activity) =
        CoroutineScope(Dispatchers.Main).launch {
            repository.changeUserGroup(oldUserBranch, oldUserSemester, updatedUser, activity)
        }

}