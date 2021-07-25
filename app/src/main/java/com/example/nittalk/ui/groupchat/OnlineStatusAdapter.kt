package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemUserStatusBinding
import com.example.nittalk.util.Comparators.USER_COMPARATOR

class OnlineStatusAdapter: ListAdapter<User, OnlineStatusAdapter.OnlineStatusViewHolder>(USER_COMPARATOR) {

    inner class OnlineStatusViewHolder(private val binding: ItemUserStatusBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                userStatusNameTextView.text = user.name
                Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(userStatusDp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineStatusViewHolder {
        val binding = ItemUserStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val statusViewHolder = OnlineStatusViewHolder(binding)
        return statusViewHolder
    }

    override fun onBindViewHolder(holderOnline: OnlineStatusViewHolder, position: Int) {
        val currentItem = getItem(position)
        holderOnline.bind(currentItem)
    }
}