package com.example.nittalk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nittalk.databinding.ItemIncomingRequestBinding
import com.example.nittalk.util.Comparators.STRING_COMPARATOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncomingRequestAdapter(
    private val listener: IncomingRequestClickListener,
    private val searchViewModel: SearchViewModel
) : ListAdapter<String, IncomingRequestAdapter.IncomingViewHolder>(
    STRING_COMPARATOR
) {

    inner class IncomingViewHolder(private val binding: ItemIncomingRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userId: String) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = searchViewModel.getUserById(userId).first()
                withContext(Dispatchers.Main) {
                    binding.apply {
                        Glide.with(binding.root).load(user.profileImageUrl).circleCrop()
                            .into(requestUserDp)
                        requestUserName.text = user.name
                        requestType.text = "Incoming Request"
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingViewHolder {
        val binding =
            ItemIncomingRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val incomingViewHolder = IncomingViewHolder(binding)
        binding.apply {
            requestAcceptButton.setOnClickListener {
                listener.acceptRequest(getItem(incomingViewHolder.absoluteAdapterPosition))
            }
            requestRejectButton.setOnClickListener {
                listener.declineRequest(getItem(incomingViewHolder.absoluteAdapterPosition))
            }
        }
        return incomingViewHolder
    }

    override fun onBindViewHolder(holder: IncomingViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}

interface IncomingRequestClickListener {
    fun acceptRequest(friendId: String)
    fun declineRequest(friendId: String)
}