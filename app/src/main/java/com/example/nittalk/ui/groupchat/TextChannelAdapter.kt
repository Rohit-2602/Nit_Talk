package com.example.nittalk.ui.groupchat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nittalk.R
import com.example.nittalk.data.TextChannel
import com.example.nittalk.databinding.ItemChannelBinding
import com.example.nittalk.util.Comparators.TEXT_CHANNEL_COMPARATOR

class TextChannelAdapter(private val listener: OnTextChannelSelected, private var selectedChannelId: LiveData<String>, private val groupChatFragment: GroupChatFragment):
    ListAdapter<TextChannel, TextChannelAdapter.TextChannelViewHolder>(TEXT_CHANNEL_COMPARATOR) {

    private val selectedChannel: String
        get() = run {
        var id = ""
        selectedChannelId.observe(groupChatFragment) {
            id = it
        }
        id
    }

    inner class TextChannelViewHolder(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(textTextChannel: TextChannel, selectedChannel: String) {
            binding.apply {
                channelTitleTextView.text = textTextChannel.channelName
                if (selectedChannel == textTextChannel.channelId) {
                    channelTitleTextView.setTextColor(Color.parseColor("#FFFFFF"))
                    hashTextView.setTextColor(Color.parseColor("#FFFFFF"))
                    channelLayout.setBackgroundResource(R.drawable.shape_channel_selected)
                }
                else {
                    channelTitleTextView.setTextColor(Color.parseColor("#6a6c71"))
                    hashTextView.setTextColor(Color.parseColor("#6a6c71"))
                    channelLayout.setBackgroundColor(Color.parseColor("#303136"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextChannelViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = TextChannelViewHolder(binding)
        viewHolder.binding.channelLayout.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            listener.showTextChannelMessages(getItem(position), getItem(position).channelId)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TextChannelViewHolder, position: Int) {
        val currentChannel = getItem(position)
        holder.bind(currentChannel, selectedChannel)
    }
}

interface OnTextChannelSelected {
    fun showTextChannelMessages(textChannel: TextChannel, channelId: String)
}