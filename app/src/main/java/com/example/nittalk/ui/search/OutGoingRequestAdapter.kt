package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.databinding.ItemOutgoingRequestBinding
import com.example.nittalk.util.Comparators.STRING_COMPARATOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OutGoingRequestAdapter(private val listener: OutGoingRequestClickListener, private val searchViewModel: SearchViewModel)
    : ListAdapter<String, OutGoingRequestAdapter.OutGoingViewHolder>(STRING_COMPARATOR) {

    inner class OutGoingViewHolder(private val binding: ItemOutgoingRequestBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(userId: String) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = searchViewModel.getUserById(userId).first()
                withContext(Dispatchers.Main) {
                    binding.apply {
                        Glide.with(binding.root).load(user.profileImageUrl).circleCrop().into(requestUserDp)
                        requestUserName.text = user.name
                        requestType.text = "Outgoing Request"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutGoingViewHolder {
        val binding = ItemOutgoingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val outgoingViewHolder = OutGoingViewHolder(binding)
        binding.apply {
            requestCancelButton.setOnClickListener {
                listener.cancelRequest(getItem(outgoingViewHolder.absoluteAdapterPosition))
            }
        }
        return outgoingViewHolder
    }

    override fun onBindViewHolder(holder: OutGoingViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}

interface OutGoingRequestClickListener {
    fun cancelRequest(friendId: String)
}