package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemFriendStatusBinding
import com.example.nittalk.util.Comparators.USER_COMPARATOR

class OnlineFriendAdapter(private val listener: ChatWithFriend) :
    ListAdapter<User, OnlineFriendAdapter.OnlineViewHolder>(USER_COMPARATOR) {

    inner class OnlineViewHolder(private val binding: ItemFriendStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                friendName.text = user.name
                friendDescription.text = "Online"
                Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(friendDp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val binding =
            ItemFriendStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val onlineViewHolder = OnlineViewHolder(binding)
        binding.friendChatButton.setOnClickListener {
            listener.navigateToFriendScreen(getItem(onlineViewHolder.absoluteAdapterPosition))
        }
        return onlineViewHolder
    }

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bind(currentFriend)
    }
}

interface ChatWithFriend {
    fun navigateToFriendScreen(friend: User)
}