package com.example.nittalk.ui.friendchat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentFriendChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendChatFragment: Fragment(R.layout.fragment_friend_chat) {

    private var _binding : FragmentFriendChatBinding?= null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFriendChatBinding.bind(view)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}