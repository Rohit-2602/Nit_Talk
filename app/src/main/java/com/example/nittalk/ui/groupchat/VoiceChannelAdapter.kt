package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nittalk.data.VoiceChannel
import com.example.nittalk.databinding.ItemVoiceChannelBinding
import com.example.nittalk.util.Comparators.VOICE_CHANNEL_COMPARATOR

class VoiceChannelAdapter(private val listener: OnVoiceChannelClicked):
    ListAdapter<VoiceChannel, VoiceChannelAdapter.VoiceChannelViewHolder>(VOICE_CHANNEL_COMPARATOR) {

    private val recyclerViewPool = RecyclerView.RecycledViewPool()
    private lateinit var voiceMemberAdapter: VoiceMemberAdapter

    inner class VoiceChannelViewHolder(private val binding: ItemVoiceChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(voiceChannel: VoiceChannel) {
            binding.channelTitleTextView.text = voiceChannel.channelName

            voiceMemberAdapter = VoiceMemberAdapter(voiceChannel.members)
            binding.membersRecyclerView.apply {
                adapter = voiceMemberAdapter
                layoutManager = LinearLayoutManager(binding.root.context)
                setRecycledViewPool(recyclerViewPool)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceChannelViewHolder {
        val binding = ItemVoiceChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = VoiceChannelViewHolder(binding)
        binding.channelLayout.setOnClickListener {
            val voiceChannel = getItem(viewHolder.absoluteAdapterPosition)
            voiceMemberAdapter.notifyDataSetChanged()
            listener.joinVoiceCall(voiceChannel)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: VoiceChannelViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

}

interface OnVoiceChannelClicked {
    fun joinVoiceCall(voiceChannel: VoiceChannel)
}