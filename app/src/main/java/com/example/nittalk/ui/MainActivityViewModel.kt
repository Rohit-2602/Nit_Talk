package com.example.nittalk.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository): ViewModel() {

    @ExperimentalCoroutinesApi
    fun makeCurrentUserOnline() =
        CoroutineScope(Dispatchers.Main).launch {
            mainActivityRepository.makeCurrentUserOnline()
        }

    @ExperimentalCoroutinesApi
    fun makeCurrentUserOffline() =
        CoroutineScope(Dispatchers.Main).launch {
            mainActivityRepository.makeCurrentUserOffline()
        }

}