package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.data.User
import com.example.nittalk.databinding.ItemSearchUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAdapter(
    private val listener: OnClickListener,
    private val searchViewModel: SearchViewModel
) : ListAdapter<User, SearchAdapter.SearchViewHolder>(USER_COMPARATOR) {

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class SearchViewHolder(private val binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            CoroutineScope(Dispatchers.IO).launch {
                val currentUser = searchViewModel.currentUser()
                withContext(Dispatchers.Main) {
                    binding.apply {
                        Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(userDp)
                        userName.text = user.name
                        if (user.id == currentUser.id) {
                            sendFriendRequestBtn.visibility = View.GONE
                            cancelFriendRequestBtn.visibility = View.GONE
                        }
                        else if (currentUser.outGoingRequests.contains(user.id)) {
                            sendFriendRequestBtn.visibility = View.GONE
                            cancelFriendRequestBtn.visibility = View.VISIBLE
                        } else {
                            sendFriendRequestBtn.visibility = View.VISIBLE
                            cancelFriendRequestBtn.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val searchViewHolder = SearchViewHolder(binding)
        binding.apply {
            sendFriendRequestBtn.setOnClickListener {
                listener.sendFriendRequest(getItem(searchViewHolder.absoluteAdapterPosition).id)
            }
            cancelFriendRequestBtn.setOnClickListener {
                listener.cancelFriendRequest(getItem(searchViewHolder.absoluteAdapterPosition).id)
            }
        }
        return searchViewHolder
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}

interface OnClickListener {
    fun sendFriendRequest(friendId: String)
    fun cancelFriendRequest(friendId: String)
}