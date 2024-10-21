package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental.Injection
import com.example.androidfundamental.NetworkResult
import com.example.androidfundamental.R
import com.example.androidfundamental.data.apimodel.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentFinishedBinding
import com.example.androidfundamental.databinding.FragmentHomeBinding
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var upcomingAdapter: AdapterUpcoming
    private lateinit var finishedAdapter: AdapterUpcoming

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel initialization
        val factory = ViewModelFactory(
            application = requireActivity().application,
            repository = Injection.provideEventRepository(),
            owner = this
        )
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        setupRecyclerView()

        // Fetch upcoming and finished events
        if (!eventViewModel.hasFetchedUpcomingEvents) {
            eventViewModel.fetchUpcomingEvents() // Default nilai query dan limit
        }

        if (!eventViewModel.hasFetchedFinishedEvents) {
            eventViewModel.fetchFinishedEvents() // Default nilai query dan limit
        }

        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Setup for Upcoming Events
        upcomingAdapter = AdapterUpcoming()
        binding.rvUpcomingHome.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingHome.adapter = upcomingAdapter

        // Setup for Finished Events
        finishedAdapter = AdapterUpcoming()
        binding.rvFinishedHome.layoutManager = LinearLayoutManager(context)
        binding.rvFinishedHome.adapter = finishedAdapter
    }

    private fun observeViewModel() {
        // Observe upcoming events
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    // Handle loading state for upcoming events
                    upcomingAdapter.showShimmerEffect()
                }

                is NetworkResult.Success -> {
                    upcomingAdapter.hideShimmerEffect()
                    result.data?.let { events ->
                        if (events.isNotEmpty()) {
                            upcomingAdapter.submitList(events)
                        } else {
                            Toast.makeText(context, "No upcoming events found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    upcomingAdapter.hideShimmerEffect()
                    Toast.makeText(context, result.message ?: "Error loading upcoming events", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe finished events
        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    // Handle loading state for finished events
                    finishedAdapter.showShimmerEffect()
                }

                is NetworkResult.Success -> {
                    finishedAdapter.hideShimmerEffect()
                    result.data?.let { events ->
                        if (events.isNotEmpty()) {
                            finishedAdapter.submitList(events)
                        } else {
                            Toast.makeText(context, "No finished events found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    finishedAdapter.hideShimmerEffect()
                    Toast.makeText(context, result.message ?: "Error loading finished events", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
