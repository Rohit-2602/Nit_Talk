package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Message
import com.example.nittalk.databinding.ItemMessageBinding
import com.example.nittalk.util.MessageTimeUtil
import java.text.DateFormat

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
        fun bind(message: Message, headerVisibility: Int, headerText: String) {
            binding.apply {
                Glide.with(root).load(message.senderDp).circleCrop().into(senderDpIV)
                senderNameTV.text = message.senderName
                messageTV.text = message.message
                val sentTime = MessageTimeUtil.getTimeAgo(message.sendAt)
                messageSentTime.text = sentTime

                messageDateHeader.visibility = headerVisibility
                messageDateHeaderText.text = headerText

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = MessageViewHolder(binding)
        binding.messageConstraint.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MessageAdapter.MessageViewHolder, position: Int) {
        val currentMessage = getItem(position)
        var headerVisibility = View.GONE
        var headerText = ""
        if (position >= 1) {
            val previousMessage = getItem(position - 1)

            val currentMessageDate = DateFormat.getDateInstance().format(currentMessage.sendAt)
            val prevMessageDate = DateFormat.getDateInstance().format(previousMessage.sendAt)

            if (currentMessageDate == prevMessageDate) {
                headerVisibility = View.GONE
            }
            else {
                headerVisibility = View.VISIBLE
                headerText = currentMessageDate
            }
        }
        else {

        }
        holder.bind(currentMessage, headerVisibility, headerText)
    }
}