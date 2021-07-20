package com.example.nittalk.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search), OnClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: SearchAdapter
    private val searchViewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.searchFragmentToolbar)

        searchViewModel.getCurrentUser().asLiveData().observe(viewLifecycleOwner) {
            Log.i("Rohit FragmentUser", it.toString())
        }

        setHasOptionsMenu(true)
        setUpSearchRecyclerView()

    }

    private fun setUpSearchRecyclerView() {
        searchAdapter = SearchAdapter(this, searchViewModel)
        searchViewModel.searchUserList.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it)
        }
        binding.searchUserRecyclerview.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
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
                    searchViewModel.searchQuery.value = newText
                }
                if (newText == "") {
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