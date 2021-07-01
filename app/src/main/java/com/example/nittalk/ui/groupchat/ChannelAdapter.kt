package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nittalk.databinding.ItemChannelBinding

class ChannelAdapter(private val channels: List<String>): RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    inner class ChannelViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(channelTitle: String) {
            binding.channelTitleTextView.text = channelTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val currentItem = channels[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return channels.size
    }
}