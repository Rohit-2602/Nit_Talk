package com.example.nittalk.ui.groupchat

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nittalk.databinding.ItemChannelBinding
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class VoiceChannelAdapter(private val context: Context, private val channels: List<String>): RecyclerView.Adapter<VoiceChannelAdapter.ChannelViewHolder>() {

    inner class ChannelViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(channelTitle: String) {
            binding.channelTitleTextView.text = channelTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val serverUrl = URL("https://meet.jit.si")
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverUrl)
            .setWelcomePageEnabled(false)
            .setAudioOnly(true)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        val binding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.channelLayout.setOnClickListener {
            val option = JitsiMeetConferenceOptions.Builder()
                .setRoom("General")
                .setWelcomePageEnabled(false)
                .build()
            JitsiMeetActivity.launch(context, option)
        }
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