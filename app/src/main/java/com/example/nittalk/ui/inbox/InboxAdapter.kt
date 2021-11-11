package com.example.nittalk.ui.inbox

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Inbox
import com.example.nittalk.databinding.ItemFriendChatBinding
import com.example.nittalk.util.Comparators.INBOX_COMPARATOR
import com.example.nittalk.util.MessageTimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InboxAdapter(
    private val friendChatViewModel: FriendChatViewModel,
    private val onFriendItemClickListener: OnFriendItemClickListener
) : ListAdapter<Inbox, InboxAdapter.InboxViewHolder>(INBOX_COMPARATOR) {

    inner class InboxViewHolder(private val binding: ItemFriendChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(inbox: Inbox) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = friendChatViewModel.getUserById(inbox.friendId)
                withContext(Dispatchers.Main) {
                    binding.apply {
                        Glide.with(binding.root).load(user.profileImageUrl).circleCrop()
                            .into(friendDp)
                        friendName.text = user.name
                        if (inbox.lastMessage == null) {
                            friendLastMessage.text = "Start Chat With ${user.name}"
                        }
                        else {
                            friendLastMessage.text = inbox.lastMessage
                        }

                        if (inbox.lastMessageTime == null) {
                            friendLastMessageTime.visibility = View.GONE
                        }
                        else {
                            val sentTime = MessageTimeUtil.getTimeAgoFriendChat(inbox.lastMessageTime!!)
                            friendLastMessageTime.text = sentTime
                        }

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        val binding =
            ItemFriendChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val friendChatViewHolder = InboxViewHolder(binding)
        binding.friendCardView.setOnClickListener {
            val current = getItem(friendChatViewHolder.absoluteAdapterPosition)
            onFriendItemClickListener.onFriendItemClick(current.friendId, current.friendName, current.lastMessage)
        }
        return friendChatViewHolder
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
}

interface OnFriendItemClickListener {
    fun onFriendItemClick(friendId: String, friendName: String, lastMessage: String?)
}