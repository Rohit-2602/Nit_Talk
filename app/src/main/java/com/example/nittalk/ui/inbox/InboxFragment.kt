package com.example.nittalk.ui.inbox

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentInboxBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InboxFragment: Fragment(R.layout.fragment_inbox), OnFriendItemClickListener {

    private var _binding : FragmentInboxBinding?= null
    private val binding get() = _binding!!
    private val friendChatViewModel by viewModels<FriendChatViewModel>()
    private lateinit var inboxAdapter: InboxAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInboxBinding.bind(view)

        setUpFriendChatRecyclerView()

        friendChatViewModel.userInbox.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.apply {
                    initialText.visibility = View.VISIBLE
                    inboxRecyclerview.visibility = View.GONE
                }
            }
            else {
                binding.apply {
                    initialText.visibility = View.GONE
                    inboxRecyclerview.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun setUpFriendChatRecyclerView() {
        inboxAdapter = InboxAdapter(friendChatViewModel, this)
        friendChatViewModel.userInbox.observe(viewLifecycleOwner) {
            inboxAdapter.submitList(it)
            inboxAdapter.notifyDataSetChanged()
        }
        binding.inboxRecyclerview.apply {
            adapter = inboxAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onFriendItemClick(friendId: String, friendName: String) {
        val action = InboxFragmentDirections.actionInboxFragmentToFriendChatFragment(friendId, friendName)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}