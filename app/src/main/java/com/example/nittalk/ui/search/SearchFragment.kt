package com.example.nittalk.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.data.User
import com.example.nittalk.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), OnClickListener,
    IncomingRequestClickListener, OutGoingRequestClickListener, ChatWithFriend {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModels<SearchViewModel>()

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var incomingRequestAdapter: IncomingRequestAdapter
    private lateinit var outGoingRequestAdapter: OutGoingRequestAdapter
    private lateinit var onlineFriendAdapter: OnlineFriendAdapter
    private lateinit var offlineFriendAdapter: OfflineFriendAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.searchFragmentToolbar)

        setHasOptionsMenu(true)
        setUpSearchRecyclerView()
        setUpIncomingRequestRecyclerView()
        setUpOnlineFriendRecyclerView()
        setUpOfflineRecyclerView()
        setUpOutGoingRequestRecyclerView()

    }

    @SuppressLint("SetTextI18n")
    private fun setUpIncomingRequestRecyclerView() {
        incomingRequestAdapter = IncomingRequestAdapter(this)
        searchViewModel.incomingRequests.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.apply {
                    incomingRequestTextView.visibility = View.GONE
                    incomingRequestRecyclerView.visibility = View.GONE
                }
            }
            else {
                binding.apply {
                    incomingRequestTextView.visibility = View.VISIBLE
                    incomingRequestRecyclerView.visibility = View.VISIBLE
                    incomingRequestTextView.text = "Incoming Requests - ${it.size}"
                }
            }
            incomingRequestAdapter.submitList(it)
        }
        binding.incomingRequestRecyclerView.apply {
            adapter = incomingRequestAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun acceptRequest(friendId: String) {
        searchViewModel.acceptFriendRequest(friendId)
    }

    override fun declineRequest(friendId: String) {
        searchViewModel.declineFriendRequest(friendId)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOutGoingRequestRecyclerView() {
        outGoingRequestAdapter = OutGoingRequestAdapter(this)
        searchViewModel.outgoingRequests.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.apply {
                    outgoingRequestTextView.visibility = View.GONE
                    outgoingRequestRecyclerView.visibility = View.GONE
                }
            }
            else {
                binding.apply {
                    outgoingRequestTextView.visibility = View.VISIBLE
                    outgoingRequestRecyclerView.visibility = View.VISIBLE
                    outgoingRequestTextView.text = "Outgoing Requests - ${it.size}"
                }
            }
            outGoingRequestAdapter.submitList(it)
        }
        binding.outgoingRequestRecyclerView.apply {
            adapter = outGoingRequestAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun cancelRequest(friendId: String) {
        searchViewModel.cancelFriendRequest(friendId)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOnlineFriendRecyclerView() {
        onlineFriendAdapter = OnlineFriendAdapter(this)
        searchViewModel.userOnlineFriends.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.apply {
                    onlineFriendsTextView.visibility = View.GONE
                    onlineFriendsRecyclerView.visibility = View.GONE
                }
            }
            else {
                binding.apply {
                    onlineFriendsTextView.visibility = View.VISIBLE
                    onlineFriendsRecyclerView.visibility = View.VISIBLE
                    onlineFriendsTextView.text = "Online - ${it.size}"
                }
            }
            onlineFriendAdapter.submitList(it)
        }
        binding.onlineFriendsRecyclerView.apply {
            adapter = onlineFriendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOfflineRecyclerView() {
        offlineFriendAdapter = OfflineFriendAdapter(this)
        searchViewModel.userOfflineFriends.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.apply {
                    offlineFriendsTextView.visibility = View.GONE
                    offlineFriendsRecyclerView.visibility = View.GONE
                }
            }
            else {
                binding.apply {
                    offlineFriendsTextView.visibility = View.VISIBLE
                    offlineFriendsRecyclerView.visibility = View.VISIBLE
                    offlineFriendsTextView.text = "Offline - ${it.size}"
                }
            }
            offlineFriendAdapter.submitList(it)
        }
        binding.offlineFriendsRecyclerView.apply {
            adapter = offlineFriendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun navigateToFriendScreen(friend: User) {
        val action = SearchFragmentDirections.actionSearchFragmentToFriendChatFragment(friend.id, friend.name)
        findNavController().navigate(action)
    }

    private fun setUpSearchRecyclerView() {
        searchAdapter = SearchAdapter(this, searchViewModel)
        searchViewModel.searchUserList.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it)
        }
        binding.searchUserRecyclerview.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun sendFriendRequest(friendId: String) {
        searchViewModel.sendFriendRequest(friendId)
    }

    override fun cancelFriendRequest(friendId: String) {
        searchViewModel.cancelFriendRequest(friendId)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_fragment_menu, menu)

        val searchItem = menu.findItem(R.id.action_search_view)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    binding.apply {
                        nestedScrollView.visibility = View.GONE
                        searchUserRecyclerview.visibility = View.VISIBLE
                    }
                    searchViewModel.searchQuery.value = newText
                }
                if (newText == "") {
                    binding.apply {
                        nestedScrollView.visibility = View.VISIBLE
                        searchUserRecyclerview.visibility = View.GONE
                    }
                    searchViewModel.searchQuery.value = "#"
                }
                return true
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}