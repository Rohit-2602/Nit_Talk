package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Message
import com.example.nittalk.databinding.ItemMessageBinding

class MessageAdapter: ListAdapter<Message, MessageAdapter.MessageViewHolder>(MESSAGE_COMPARATOR) {

    companion object {
        private val MESSAGE_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.apply {
                Glide.with(root).load(message.senderDp).circleCrop().into(senderDpIV)
                senderNameTV.text = message.senderName
                messageTV.text = message.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = MessageViewHolder(binding)
        binding.messageConstraint.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = getItem(position)
        holder.bind(currentMessage)
    }
}