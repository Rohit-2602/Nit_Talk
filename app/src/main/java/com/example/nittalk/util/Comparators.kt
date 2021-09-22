package com.example.nittalk.util

import androidx.recyclerview.widget.DiffUtil
import com.example.nittalk.data.*

object Comparators {

    val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val INBOX_COMPARATOR = object : DiffUtil.ItemCallback<Inbox>() {
        override fun areItemsTheSame(oldItem: Inbox, newItem: Inbox): Boolean {
            return oldItem.friendId == newItem.friendId
        }

        override fun areContentsTheSame(oldItem: Inbox, newItem: Inbox): Boolean {
            return oldItem == newItem
        }
    }

    val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
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

    val TEXT_CHANNEL_COMPARATOR = object : DiffUtil.ItemCallback<TextChannel>() {
        override fun areItemsTheSame(oldItem: TextChannel, newItem: TextChannel): Boolean {
            return oldItem.channelId == newItem.channelId
        }
        override fun areContentsTheSame(oldItem: TextChannel, newItem: TextChannel): Boolean {
            return oldItem == newItem
        }
    }

    val VOICE_CHANNEL_COMPARATOR = object : DiffUtil.ItemCallback<VoiceChannel>() {
        override fun areItemsTheSame(oldItem: VoiceChannel, newItem: VoiceChannel): Boolean {
            return oldItem.channelId == newItem.channelId
        }
        override fun areContentsTheSame(oldItem: VoiceChannel, newItem: VoiceChannel): Boolean {
            return oldItem == newItem
        }
    }

}