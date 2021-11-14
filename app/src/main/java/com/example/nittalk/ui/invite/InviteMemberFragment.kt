package com.example.nittalk.ui.invite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.data.Inbox
import com.example.nittalk.databinding.FragmentInviteMemberBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteMemberFragment : Fragment(R.layout.fragment_invite_member), InviteMemberListener {

    private var _binding: FragmentInviteMemberBinding? = null
    private val binding get() = _binding!!
    private val inviteMemberViewModel by viewModels<InviteMemberViewModel>()
    private val navArgs by navArgs<InviteMemberFragmentArgs>()
    private lateinit var inviteFriendAdapter: InviteFriendAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInviteMemberBinding.bind(view)

        inviteFriendAdapter = InviteFriendAdapter(this)

        inviteMemberViewModel.searchUserList.asLiveData().observe(viewLifecycleOwner) {
            inviteFriendAdapter.submitList(it)
        }

        binding.inviteFriendRecyclerview.apply {
            adapter = inviteFriendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.apply {
            closeBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            searchFriendEdittext.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (newText != null) {
                        inviteMemberViewModel.searchQuery.value = newText.toString()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }

    }

    override fun inviteMember(inbox: Inbox) {
        inviteMemberViewModel.sendPersonalMessage(
            currentUser = navArgs.currentUser,
            friendId = inbox.friendId,
            imageUrl = "",
            messageText = "Join this Server",
            repliedTo = null,
            joinGroup = navArgs.group
        ).invokeOnCompletion {
            Toast.makeText(requireContext(), "Invitation Sent", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}