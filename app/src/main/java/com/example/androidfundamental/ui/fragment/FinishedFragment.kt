package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidfundamental.data.apimodel.ListEventsItem
import com.example.androidfundamental.data.apimodel.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentFinishedBinding
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class FinishedFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: AdapterUpcoming
    private lateinit var binding: FragmentFinishedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(requireContext())
        eventViewModel = ViewModelProvider(this, factory).get(EventViewModel::class.java)

        adapter = AdapterUpcoming()
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.layoutManager = layoutManager
        binding.rvFinished.adapter = adapter

        eventViewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        setupSearch()

        eventViewModel.fetchEvents(active = -1)

        adapter.setOnItemClickListener(object : AdapterUpcoming.OnItemClickAdapter {
            override fun onItemClick(data: ListEventsItem) {
                Toast.makeText(context, "Clicked: ${data.name}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearch() {
        // Show SearchView when SearchBar is clicked
        binding.searchBar.setOnClickListener {
            binding.searchView.show() // Show the Material SearchView
        }

        // Handle query submission from SearchView
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            if (query.isNotBlank()) {
                eventViewModel.fetchEvents(active = -1, query = query)
            }
            binding.searchView.hide() // Hide SearchView after search
            true
        }

        // Optional: Handle query text change
        binding.searchView.editText.addTextChangedListener {
            val query = it.toString()
            if (query.isEmpty()) {
                // Optionally refresh all events when query is cleared
                eventViewModel.fetchEvents(active = -1)
            }
        }
    }

}
