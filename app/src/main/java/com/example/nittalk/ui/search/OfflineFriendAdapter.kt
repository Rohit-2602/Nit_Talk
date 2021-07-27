package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemFriendStatusBinding
import com.example.nittalk.util.Comparators.USER_COMPARATOR

class OfflineFriendAdapter :
    ListAdapter<User, OfflineFriendAdapter.OfflineViewHolder>(USER_COMPARATOR) {

    inner class OfflineViewHolder(private val binding: ItemFriendStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                friendName.text = user.name
                friendDescription.text = "Offline"
                Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(friendDp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfflineViewHolder {
        val binding =
            ItemFriendStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val offlineViewHolder = OfflineViewHolder(binding)
        return offlineViewHolder
    }

    override fun onBindViewHolder(holder: OfflineViewHolder, position: Int) {
        val currentFriend = getItem(position)
        holder.bind(currentFriend)
    }
}