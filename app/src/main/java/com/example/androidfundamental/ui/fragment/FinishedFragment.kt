package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidfundamental.R
import com.example.androidfundamental.data.Resource
import com.example.androidfundamental.ui.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentFinishedBinding
import com.example.androidfundamental.di.Injection
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.google.android.material.search.SearchView


class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterUpcoming
    private var currentQuery: String? = null
    private var scrollState: IntArray? = null

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(
            repository = Injection.provideEventRepository(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        adapter.setOnItemClickListener { data ->
            val bundle = Bundle().apply {
                putInt("event_id", data.id)
            }
            findNavController().navigate(R.id.action_finishedFragment_to_detailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = AdapterUpcoming()
        binding.rvFinished.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.adapter = adapter
        adapter.showShimmerEffect()
    }

    private fun setupSearch() {
        binding.searchBar.setOnClickListener {
            binding.searchView.show()
        }

        binding.searchView.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchView.text.toString().trim()
                currentQuery = query.ifBlank { null }
                eventViewModel.searchFinishedEvents(currentQuery)
                binding.searchView.hide()
                true
            } else {
                false
            }
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN && binding.searchView.text.isBlank()) {
                currentQuery = null
                eventViewModel.searchFinishedEvents(null)
            }
        }
    }

    private fun observeViewModel() {
        eventViewModel.finalFinishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    saveScrollPosition()
                    adapter.showShimmerEffect()
                }
                is Resource.Success -> {
                    adapter.hideShimmerEffect()
                    val events = result.data
                    adapter.submitList(events)
                    restoreScrollPosition()
                }
                is Resource.Error -> {
                    adapter.hideShimmerEffect()
                    adapter.submitList(emptyList())
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun saveScrollPosition() {
        val layoutManager = binding.rvFinished.layoutManager as StaggeredGridLayoutManager
        scrollState = layoutManager.findFirstVisibleItemPositions(null)
    }

    private fun restoreScrollPosition() {
        scrollState?.let {
            val layoutManager = binding.rvFinished.layoutManager as StaggeredGridLayoutManager
            layoutManager.scrollToPositionWithOffset(it[0], 0)
        }
    }
}