package com.example.nittalk.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel @ViewModelInject constructor(private val mainActivityRepository: MainActivityRepository): ViewModel() {

    fun makeCurrentUserOnline() =
        CoroutineScope(Dispatchers.Main).launch {
            mainActivityRepository.makeCurrentUserOnline()
        }

    fun makeCurrentUserOffline() =
        CoroutineScope(Dispatchers.Main).launch {
            mainActivityRepository.makeCurrentUserOffline()
        }

}