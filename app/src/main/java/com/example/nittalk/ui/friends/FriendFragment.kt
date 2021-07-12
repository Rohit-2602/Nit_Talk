package com.example.nittalk.ui.friends

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentFriendBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendFragment : Fragment(R.layout.fragment_friend) {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFriendBinding.bind(view)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}