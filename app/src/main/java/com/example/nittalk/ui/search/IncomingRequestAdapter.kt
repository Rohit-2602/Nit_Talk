package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemIncomingRequestBinding
import com.example.nittalk.util.Comparators.USER_COMPARATOR

class IncomingRequestAdapter(private val listener: IncomingRequestClickListener) :
    ListAdapter<User, IncomingRequestAdapter.IncomingViewHolder>(USER_COMPARATOR) {

    inner class IncomingViewHolder(private val binding: ItemIncomingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(binding.root).load(user.profileImageUrl).circleCrop()
                    .into(requestUserDp)
                requestUserName.text = user.name
                requestType.text = "Incoming Request"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingViewHolder {
        val binding =
            ItemIncomingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val incomingViewHolder = IncomingViewHolder(binding)
        binding.apply {
            requestAcceptButton.setOnClickListener {
                listener.acceptRequest(getItem(incomingViewHolder.absoluteAdapterPosition).id)
            }
            requestRejectButton.setOnClickListener {
                listener.declineRequest(getItem(incomingViewHolder.absoluteAdapterPosition).id)
            }
        }
        return incomingViewHolder
    }

    override fun onBindViewHolder(holder: IncomingViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}

interface IncomingRequestClickListener {
    fun acceptRequest(friendId: String)
    fun declineRequest(friendId: String)
}