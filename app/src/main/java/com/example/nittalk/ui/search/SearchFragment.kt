package com.example.nittalk.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), OnClickListener, IncomingRequestClickListener, OutGoingRequestClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
//    private lateinit var searchAdapter: SearchAdapter
    private lateinit var incomingRequestAdapter : IncomingRequestAdapter
//    private lateinit var outGoingRequestAdapter : OutGoingRequestAdapter
//    private lateinit var onlineFriendAdapter: OnlineFriendAdapter
//    private lateinit var offlineFriendAdapter: OfflineFriendAdapter
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.searchFragmentToolbar)

//        setHasOptionsMenu(true)
//        setUpSearchRecyclerView()
        setUpIncomingRequestRecyclerView()
//        setUpOutGoingRequestRecyclerView()
//        setUpOnlineFriendRecyclerView()
//        setUpOfflineRecyclerView()

        searchViewModel.incomingRequests.observe(viewLifecycleOwner) {
            Log.i("Rohit IncomingRequest", it.toString())
            incomingRequestAdapter.submitList(it)
            incomingRequestAdapter.notifyDataSetChanged()
        }

//        searchViewModel.outgoingRequests.observe(viewLifecycleOwner) {
//            outGoingRequestAdapter.submitList(it)
//            outGoingRequestAdapter.notifyDataSetChanged()
//        }
//
//        searchViewModel.userFriends.observe(viewLifecycleOwner) {
//            onlineFriendAdapter.submitList(it)
//            onlineFriendAdapter.notifyDataSetChanged()
//        }
//
//        searchViewModel.userFriends.observe(viewLifecycleOwner) {
//            offlineFriendAdapter.submitList(it)
//            offlineFriendAdapter.notifyDataSetChanged()
//        }

    }

    private fun setUpIncomingRequestRecyclerView() {
        incomingRequestAdapter = IncomingRequestAdapter(this, searchViewModel)
//        searchViewModel.incomingRequests.observe(viewLifecycleOwner) {
//            incomingRequestAdapter.submitList(it)
//            incomingRequestAdapter.notifyDataSetChanged()
//        }
        binding.incomingRequestRecyclerView.apply {
            adapter = incomingRequestAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun acceptRequest(friendId: String) {
        searchViewModel.acceptFriendRequest(friendId)
    }

    override fun declineRequest(friendId: String) {
        searchViewModel.declineFriendRequest(friendId)
    }

//    private fun setUpOutGoingRequestRecyclerView() {
//        outGoingRequestAdapter = OutGoingRequestAdapter(this, searchViewModel)
////        searchViewModel.outgoingRequests.observe(viewLifecycleOwner) {
////            outGoingRequestAdapter.submitList(it)
////            outGoingRequestAdapter.notifyDataSetChanged()
////        }
//        binding.outgoingRequestRecyclerView.apply {
//            adapter = outGoingRequestAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }
//    }

    override fun cancelRequest(friendId: String) {
        searchViewModel.cancelFriendRequest(friendId)
    }

//    private fun setUpOnlineFriendRecyclerView() {
//        onlineFriendAdapter = OnlineFriendAdapter(searchViewModel)
////        searchViewModel.userFriends.observe(viewLifecycleOwner) {
////            onlineFriendAdapter.submitList(it)
////            onlineFriendAdapter.notifyDataSetChanged()
////        }
//        binding.onlineFriendsRecyclerView.apply {
//            adapter = onlineFriendAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }
//    }
//
//    private fun setUpOfflineRecyclerView() {
//        offlineFriendAdapter = OfflineFriendAdapter(searchViewModel)
////        searchViewModel.userFriends.observe(viewLifecycleOwner) {
////            offlineFriendAdapter.submitList(it)
////            offlineFriendAdapter.notifyDataSetChanged()
////        }
//        binding.offlineFriendsRecyclerView.apply {
//            adapter = offlineFriendAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }
//    }

//    private fun setUpSearchRecyclerView() {
//        searchAdapter = SearchAdapter(this, searchViewModel)
//        searchViewModel.searchUserList.observe(viewLifecycleOwner) {
//            searchAdapter.submitList(it)
//        }
//        binding.searchUserRecyclerview.apply {
//            adapter = searchAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }
//    }

    override fun sendFriendRequest(friendId: String) {
        searchViewModel.sendFriendRequest(friendId)
    }

    override fun cancelFriendRequest(friendId: String) {
        searchViewModel.cancelFriendRequest(friendId)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//
//        inflater.inflate(R.menu.search_fragment_menu, menu)
//
//        val searchItem = menu.findItem(R.id.action_search_view)
//        val searchView = searchItem.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText != null) {
//                    searchViewModel.searchQuery.value = newText
//                }
//                if (newText == "") {
//                    searchViewModel.searchQuery.value = "#"
//                }
//                return true
//            }
//        })
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}