package com.example.nittalk.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(private val mainActivityRepository: MainActivityRepository): ViewModel() {

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