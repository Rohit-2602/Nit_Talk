package com.example.nittalk.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var _binding : FragmentProfileBinding ?= null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.signOutBtn.setOnClickListener {
            viewModel.signOut(requireActivity())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}