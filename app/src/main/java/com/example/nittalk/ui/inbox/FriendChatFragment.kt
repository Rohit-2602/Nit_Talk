package com.example.nittalk.ui.inbox

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentFriendChatBinding
import com.example.nittalk.ui.groupchat.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendChatFragment: Fragment(R.layout.fragment_friend_chat) {

    private var _binding: FragmentFriendChatBinding?= null
    private val binding get() = _binding!!
    private val friendChatViewModel by viewModels<FriendChatViewModel>()
    private val navArgs by navArgs<FriendChatFragmentArgs>()
    private lateinit var messageAdapter : MessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFriendChatBinding.bind(view)

        binding.apply {
            friendChatToolbar.title = navArgs.friendName
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.apply {
            messageEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun afterTextChanged(p0: Editable?) {
                    val isEmpty = p0.toString().trim().isEmpty()
                    if (isEmpty) {
                        messageSendBtn.visibility = View.GONE
                    } else {
                        messageSendBtn.visibility = View.VISIBLE
                    }
                }
            })

            messageSendBtn.setOnClickListener {
                val message = messageEditText.text.toString().trim()
                if (message != "") {
                    messageEditText.setText("")
                    friendChatViewModel.sendPersonalMessage(
                        friendId = navArgs.friendId,
                        messageText = message,
                        imageUrl = ""
                    )
                } else {
                    Toast.makeText(requireContext(), "Write Something", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setUpFriendChatRecyclerView()

    }

    private fun setUpFriendChatRecyclerView() {
        messageAdapter = MessageAdapter()
        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.friendChatRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = mLayoutManager
        }
        mLayoutManager.stackFromEnd = true
        friendChatViewModel.getFriendMessages(navArgs.friendId).observe(viewLifecycleOwner) {
            messageAdapter.submitList(it)
            mLayoutManager.smoothScrollToPosition(binding.friendChatRecyclerView, null, it.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}