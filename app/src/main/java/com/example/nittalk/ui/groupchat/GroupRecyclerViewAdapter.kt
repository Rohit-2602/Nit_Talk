package com.example.nittalk.ui.groupchat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.Group
import com.example.nittalk.databinding.ItemGroupBinding

class GroupRecyclerViewAdapter(private val listener: OnGroupItemSelected, private var selectedGroupId: String) :
    ListAdapter<Group, GroupRecyclerViewAdapter.GroupViewHolder>(GROUP_COMPARATOR) {

    companion object {
        private val GROUP_COMPARATOR = object : DiffUtil.ItemCallback<Group>() {
            override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem.groupId == newItem.groupId
            }
            override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem == newItem
            }
        }
    }

    class GroupViewHolder(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            Glide.with(binding.root).load(group.groupDp).circleCrop().into(binding.chatDP)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val viewHolder = GroupViewHolder(binding)
        viewHolder.binding.groupLayout.setOnClickListener {
            val position = viewHolder.absoluteAdapterPosition
            selectedGroupId = getItem(position).groupId
            listener.checkOutGroup(getItem(position).groupId)
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = getItem(position)
        if (selectedGroupId == currentGroup.groupId) {
            holder.binding.selectedView.visibility = View.VISIBLE
        }
        else {
            holder.binding.selectedView.visibility = View.GONE
        }
        holder.bind(currentGroup)
    }
}

interface OnGroupItemSelected {
    fun checkOutGroup(groupId: String)
}