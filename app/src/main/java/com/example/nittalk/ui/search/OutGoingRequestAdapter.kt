package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemOutgoingRequestBinding
import com.example.nittalk.util.Comparators.USER_COMPARATOR

class OutGoingRequestAdapter(private val listener: OutGoingRequestClickListener) :
    ListAdapter<User, OutGoingRequestAdapter.OutGoingViewHolder>(USER_COMPARATOR) {

    inner class OutGoingViewHolder(private val binding: ItemOutgoingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(requestUserDp)
                requestUserName.text = user.name
                requestType.text = "Outgoing Request"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutGoingViewHolder {
        val binding =
            ItemOutgoingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val outgoingViewHolder = OutGoingViewHolder(binding)
        binding.apply {
            requestCancelButton.setOnClickListener {
                listener.cancelRequest(getItem(outgoingViewHolder.absoluteAdapterPosition).id)
            }
        }
        return outgoingViewHolder
    }

    override fun onBindViewHolder(holder: OutGoingViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}

interface OutGoingRequestClickListener {
    fun cancelRequest(friendId: String)
}