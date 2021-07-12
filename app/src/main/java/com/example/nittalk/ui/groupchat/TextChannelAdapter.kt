package com.example.nittalk.ui.groupchat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nittalk.R
import com.example.nittalk.data.Channel
import com.example.nittalk.databinding.ItemChannelBinding

class TextChannelAdapter(private val listener: OnTextChannelSelected, private var selectedChannelId: LiveData<String>, private val groupChatFragment: GroupChatFragment):
    ListAdapter<Channel, TextChannelAdapter.TextChannelViewHolder>(TEXT_CHANNEL_COMPARATOR) {

    private val selectedChannel: String
        get() = run {
        var id = ""
        selectedChannelId.observe(groupChatFragment) {
            id = it
        }
        id
    }

    companion object {
        private val TEXT_CHANNEL_COMPARATOR = object : DiffUtil.ItemCallback<Channel>() {
            override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem.channelId == newItem.channelId
            }
            override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TextChannelViewHolder(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(textChannel: Channel) {
            binding.channelTitleTextView.text = textChannel.channelName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextChannelViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = TextChannelViewHolder(binding)
        viewHolder.binding.channelLayout.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            listener.showTextChannelMessages(getItem(position), getItem(position).channelId)
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TextChannelViewHolder, position: Int) {
        val currentChannel = getItem(position)
        if (selectedChannel == currentChannel.channelId) {
            holder.binding.channelTitleTextView.setTextColor(Color.parseColor("#FFFFFF"))
            holder.binding.hashTextView.setTextColor(Color.parseColor("#FFFFFF"))
            holder.binding.channelLayout.setBackgroundResource(R.drawable.shape_channel_selected)
        }
        else {
            holder.binding.channelTitleTextView.setTextColor(Color.parseColor("#6a6c71"))
            holder.binding.hashTextView.setTextColor(Color.parseColor("#6a6c71"))
            holder.binding.channelLayout.setBackgroundColor(Color.parseColor("#303136"))
        }
        holder.bind(currentChannel)
    }
}

interface OnTextChannelSelected {
    fun showTextChannelMessages(channel: Channel, channelId: String)
}