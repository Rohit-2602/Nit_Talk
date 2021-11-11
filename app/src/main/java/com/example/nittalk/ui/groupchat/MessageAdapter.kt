package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Message
import com.example.nittalk.databinding.ItemMessageBinding
import com.example.nittalk.util.Comparators.MESSAGE_COMPARATOR
import com.example.nittalk.util.MessageTimeUtil
import java.text.DateFormat

class MessageAdapter(private val listener: OnMessageLongPress):
    ListAdapter<Message, MessageAdapter.MessageViewHolder>(MESSAGE_COMPARATOR) {

    inner class MessageViewHolder(private val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, headerVisibility: Int, headerText: String) {
            binding.apply {
                Glide.with(root).load(message.senderDp).circleCrop().into(senderDpIV)
                Glide.with(root).load(message.imageUrl).into(messageImage)
                senderNameTV.text = message.senderName
                if (message.edited) {
                    messageEditedTV.visibility = View.VISIBLE
                }
                else {
                    messageEditedTV.visibility = View.GONE
                }
                messageTV.text = message.message
                val sentTime = MessageTimeUtil.getTimeAgoGroupChat(message.sendAt)
                messageSentTime.text = sentTime

                messageDateHeader.visibility = headerVisibility
                messageDateHeaderText.text = headerText

                if (message.repliedTo != null) {
                    repliedMessageLayout.visibility = View.VISIBLE
                    repliedMessageSenderName.text = message.repliedTo?.senderName
                    repliedMessageText.text = message.repliedTo?.message
                    Glide.with(root).load(message.repliedTo?.senderDp).circleCrop().into(repliedMessageSenderDp)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = MessageViewHolder(binding)
        binding.messageConstraint.setOnLongClickListener {
            if(itemCount >= 2) {
                listener.showMessageOptions(getItem(viewHolder.absoluteAdapterPosition), currentList[itemCount-1], currentList[itemCount-2])
            }
            else {
                listener.showMessageOptions(getItem(viewHolder.absoluteAdapterPosition), currentList[itemCount-1], null)
            }
            return@setOnLongClickListener true
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
        holder.bind(currentMessage, headerVisibility, headerText)
    }
}

interface OnMessageLongPress {
    fun showMessageOptions(message: Message, lastMessage: Message, nextLastMessage: Message?)
}