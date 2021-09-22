package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemVoiceMembersBinding

class VoiceMemberAdapter(private val membersList: List<User>): RecyclerView.Adapter<VoiceMemberAdapter.VoiceViewHolder>() {

    inner class VoiceViewHolder(private val binding: ItemVoiceMembersBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindMember(member: User) {
            binding.apply {
                userNameTextView.text = member.name
                Glide.with(binding.root).load(member.profileImageUrl).circleCrop().into(userDpImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {
        val voiceMembersBinding = ItemVoiceMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val voiceViewHolder = VoiceViewHolder(voiceMembersBinding)
        return voiceViewHolder
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        val member = membersList[position]
        holder.bindMember(member)
    }

    override fun getItemCount(): Int {
        return membersList.size
    }
}