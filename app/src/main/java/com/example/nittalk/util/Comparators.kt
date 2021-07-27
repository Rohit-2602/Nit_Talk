package com.example.nittalk.util

import androidx.recyclerview.widget.DiffUtil
import com.example.nittalk.data.Channel
import com.example.nittalk.data.Group
import com.example.nittalk.data.User

object Comparators {

    val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.groupId == newItem.groupId
        }
        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }
    }

    val TEXT_CHANNEL_COMPARATOR = object : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.channelId == newItem.channelId
        }
        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }

}