package com.example.nittalk.ui.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Inbox
import com.example.nittalk.databinding.ItemInviteMemberBinding
import com.example.nittalk.util.Comparators.INBOX_COMPARATOR

class InviteFriendAdapter(private val listener: InviteMemberListener) :
    ListAdapter<Inbox, InviteFriendAdapter.InviteFriendViewHolder>(INBOX_COMPARATOR) {

    inner class InviteFriendViewHolder(private val binding: ItemInviteMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bindUser(inbox: Inbox) {
                binding.apply {
                    userName.text = inbox.friendName
                    Glide.with(binding.root).load(inbox.friendDp).circleCrop().into(friendProfileImage)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteFriendViewHolder {
        val binding = ItemInviteMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val inviteFriendViewHolder = InviteFriendViewHolder(binding)
        binding.apply {
            inviteFriendButton.setOnClickListener {
                listener.inviteMember(getItem(inviteFriendViewHolder.absoluteAdapterPosition))
                inviteFriendButton.text = "Invited"
                inviteFriendButton.isEnabled = false
            }
        }
        return inviteFriendViewHolder
    }

    override fun onBindViewHolder(holder: InviteFriendViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindUser(currentItem)
    }
}

interface InviteMemberListener {
    fun inviteMember(inbox: Inbox)
}