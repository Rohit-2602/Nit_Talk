package com.example.nittalk.ui.groupchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.data.Channel
import com.example.nittalk.data.Group
import com.example.nittalk.data.User
import com.example.nittalk.databinding.FragmentGroupChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GroupChatFragment : Fragment(R.layout.fragment_group_chat), OnGroupItemSelected,
    OnTextChannelSelected {

    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    private val groupChatViewModel by viewModels<GroupChatViewModel>()
    private lateinit var groupAdapter : GroupAdapter
    private lateinit var textChannelAdapter: TextChannelAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var onlineAdapter: OnlineStatusAdapter
    private lateinit var offlineAdapter: OfflineStatusAdapter
    private lateinit var currentGroup : Group
    private lateinit var currentUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupChatBinding.bind(view)
        setUpNavDrawer()

        groupChatViewModel.currentGroup.asLiveData().observe(viewLifecycleOwner) {
            currentGroup = it
        }

        groupChatViewModel.groupName.observe(viewLifecycleOwner) {
            binding.groupNameTextView.text = it
        }

        groupChatViewModel.currentUserFromDB.asLiveData().observe(viewLifecycleOwner) {
            currentUser = it
        }

        binding.apply {
            textChannelsTextView.setOnClickListener {
                if (binding.textChannelsRecyclerview.isVisible) {
                    binding.textChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_down,
                        0,
                        0,
                        0
                    )
                    binding.textChannelsRecyclerview.visibility = View.GONE
                } else {
                    binding.textChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                    binding.textChannelsRecyclerview.visibility = View.VISIBLE
                }
            }
        }

        binding.apply {
            voiceChannelsTextView.setOnClickListener {
                if (voiceChannelsRecyclerview.isVisible) {
                    voiceChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_down,
                        0,
                        0,
                        0
                    )
                    voiceChannelsRecyclerview.visibility = View.GONE
                } else {
                    voiceChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                    voiceChannelsRecyclerview.visibility = View.VISIBLE
                }
            }
        }

        setUpGroupRecyclerView()
        setUpTextChannelRecyclerView()
        setUpChannelsRecyclerView()
        setUpMessageRecyclerView()
        setUpOnlineUserRecyclerView()
        setUpOfflineUserRecyclerView()

        binding.apply {
            messageEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun afterTextChanged(p0: Editable?) {
                    val isEmpty = p0.toString().trim().isEmpty()
                    if (isEmpty) {
                        messageSendBtn.visibility = View.GONE
                    } else {
                        messageSendBtn.visibility = View.VISIBLE
                    }
                }
            })

            messageSendBtn.setOnClickListener {
                val message = messageEditText.text.toString().trim()
                if (message != "") {
                    messageEditText.setText("")
                    groupChatViewModel.sendMessage(messageText = message, imageUrl = "")
                    val membersUidList = currentGroup.members

                    for (member in membersUidList) {
                        groupChatViewModel.sendNotification(
                            requireContext(),
                            currentGroup.groupName,
                            message = currentUser.name + ": " + message,
                            userId = groupChatViewModel.currentUserUid
                        )
                    }

                }
                else {
                    Toast.makeText(requireContext(), "Write Something", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setUpGroupRecyclerView() {
        groupAdapter = GroupAdapter(this, groupChatViewModel.selectedGroupId)
        groupChatViewModel.currentUserGroups.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }
        binding.groupRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun checkOutGroup(groupId: String) {
        groupChatViewModel.updateGroupSelected(groupId)
        groupChatViewModel.update(groupId)
        messageAdapter.notifyDataSetChanged()
    }

    private fun setUpTextChannelRecyclerView() {
        textChannelAdapter = TextChannelAdapter(this, groupChatViewModel.channelSelected, this)
        groupChatViewModel.textChannels.asLiveData().observe(viewLifecycleOwner) {
            textChannelAdapter.submitList(it)
        }
        binding.textChannelsRecyclerview.apply {
            adapter = textChannelAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun showTextChannelMessages(channel: Channel, channelId: String) {
        groupChatViewModel.updateChannelSelected(channel.groupId, channelId)
        binding.apply {
            groupChatToolbar.title = channel.channelName
            drawerLayout.closeDrawer(GravityCompat.START)
            messageEditText.setText("")
        }
        Toast.makeText(requireContext(), "Clicked On ${channel.channelName}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setUpMessageRecyclerView() {
        messageAdapter = MessageAdapter()
        val mLayoutManager = LinearLayoutManager(requireContext())
        groupChatViewModel.messages.asLiveData().observe(viewLifecycleOwner) {
            messageAdapter.submitList(it)
            mLayoutManager.smoothScrollToPosition(binding.messageRV, null, it.size)
        }
        mLayoutManager.stackFromEnd = true
        binding.messageRV.apply {
            adapter = messageAdapter
            layoutManager = mLayoutManager
            setHasFixedSize(true)
        }
    }

    private fun setUpOnlineUserRecyclerView() {
        onlineAdapter = OnlineStatusAdapter()
        groupChatViewModel.onlineGroupMembers.observe(viewLifecycleOwner) {
            onlineAdapter.submitList(it)
            binding.onlineTextView.text = "Online - ${it.size}"
        }
        binding.onlineRecyclerView.apply {
            adapter = onlineAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setUpOfflineUserRecyclerView() {
        offlineAdapter = OfflineStatusAdapter()
        groupChatViewModel.offlineGroupMembers.observe(viewLifecycleOwner) {
            offlineAdapter.submitList(it)
            binding.offlineTextView.text = "Offline - ${it.size}"
        }
        binding.offlineRecyclerView.apply {
            adapter = offlineAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setUpNavDrawer() {
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.groupChatToolbar)

        CoroutineScope(Dispatchers.Main).launch {
            groupChatViewModel.channelName.observe(viewLifecycleOwner) { channelName ->
                binding.groupChatToolbar.title = channelName
                binding.onlineChannelTitle.text = channelName
            }
        }

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        toggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                textChannelAdapter.notifyDataSetChanged()
                onlineAdapter.notifyDataSetChanged()
                offlineAdapter.notifyDataSetChanged()
            }
            override fun onDrawerOpened(drawerView: View) {
                textChannelAdapter.notifyDataSetChanged()
                onlineAdapter.notifyDataSetChanged()
                offlineAdapter.notifyDataSetChanged()
                bottomNav.visibility = View.VISIBLE
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                bottomNav.visibility = View.GONE
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("RtlHardcoded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.onlineStatus) {
            if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            } else {
                binding.drawerLayout.openDrawer(Gravity.RIGHT)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpChannelsRecyclerView() {
        val channels = listOf(
            "# Channel 1",
            "# Channel 1",
            "# Channel 1",
            "# Channel 1",
            "# Channel 1",
            "# Channel 1",
            "# Channel 1",
            "# Channel 1"
        )
        val channelAdapter = ChannelAdapter(channels)

        binding.voiceChannelsRecyclerview.apply {
            adapter = channelAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}