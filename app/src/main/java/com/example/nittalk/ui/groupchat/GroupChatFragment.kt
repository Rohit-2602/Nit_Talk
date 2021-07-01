package com.example.nittalk.ui.groupchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.data.Channel
import com.example.nittalk.data.Group
import com.example.nittalk.databinding.FragmentGroupChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupChatFragment : Fragment(R.layout.fragment_group_chat), OnGroupItemSelected,
    OnTextChannelSelected {

    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel by viewModels<GroupChatViewModel>()
    private lateinit var groupAdapter : GroupRecyclerViewAdapter
    private lateinit var textChannelAdapter: TextChannelRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupChatBinding.bind(view)
        setUpNavDrawer()

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

    }

    private fun setUpGroupRecyclerView() {
        groupAdapter = GroupRecyclerViewAdapter(this, viewModel.selectedGroupId)
        viewModel.currentUserGroups.observe(viewLifecycleOwner) {
            binding.groupNameTextView.text = it[0].groupName
            groupAdapter.submitList(it)
        }
        binding.groupRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun checkOutGroup(group: Group, groupId: String) {
        binding.groupNameTextView.text = group.groupName
        viewModel.updateGroupSelected(groupId)
    }

    private fun setUpTextChannelRecyclerView() {
        textChannelAdapter = TextChannelRecyclerViewAdapter(this, viewModel.selectedChannelId)
        viewModel.textChannels.asLiveData().observe(viewLifecycleOwner) {
            Log.i("Rohit Channels", it.toString())
            textChannelAdapter.submitList(it)
        }
        binding.textChannelsRecyclerview.apply {
            adapter = textChannelAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun showTextChannelMessages(channel: Channel, channelId: String) {
        viewModel.updateChannelSelected(channelId)
        Toast.makeText(requireContext(), "Clicked On ${channel.channelName}", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setUpNavDrawer() {
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.groupChatToolbar)
        mainActivity.setupActionBarWithNavController(findNavController())

        toggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                groupAdapter.notifyDataSetChanged()
                textChannelAdapter.notifyDataSetChanged()
            }

            override fun onDrawerOpened(drawerView: View) {
                groupAdapter.notifyDataSetChanged()
                textChannelAdapter.notifyDataSetChanged()
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