package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental.utils.Injection
import com.example.androidfundamental.utils.NetworkResult
import com.example.androidfundamental.R
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.example.androidfundamental.data.apimodel.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentUpcomingBinding


class UpcomingFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: AdapterUpcoming
    private lateinit var binding: FragmentUpcomingBinding
    private var currentQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(
            application = requireActivity().application,
            repository = Injection.provideEventRepository(),
            owner = this
        )

        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        // Restore state jika ada (gunakan savedStateHandle)
        savedInstanceState?.let {
            currentQuery = it.getString("lastQuery")
            eventViewModel.hasFetchedUpcomingEvents = it.getBoolean("hasFetchedEvents", false)
        }

        setupRecyclerView()
        observeViewModel()

        if (!eventViewModel.hasFetchedUpcomingEvents) {
            eventViewModel.fetchUpcomingEvents()
        }

        adapter.setOnItemClickListener { data ->
            val bundle = Bundle().apply {
                putInt("event_id", data.id ?: 0)
            }
            findNavController().navigate(R.id.action_upcomingFragment_to_detailFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        adapter = AdapterUpcoming()
        binding.rvUpcoming.layoutManager = LinearLayoutManager(context)
        binding.rvUpcoming.adapter = adapter
        adapter.showShimmerEffect()
    }

    private fun observeViewModel() {
        // Mengamati perubahan pada events dengan NetworkResult
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    showLoadingIndicator()
                    adapter.showShimmerEffect()
                }

                is NetworkResult.Success -> {
                    adapter.hideShimmerEffect()
                    hideLoadingIndicator()

                    result.data?.let { events ->
                        if (events.isNotEmpty()) {
                            adapter.submitList(events)
                        } else {
                            adapter.submitList(emptyList())
                            Toast.makeText(context, "No events found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    adapter.hideShimmerEffect()
                    hideLoadingIndicator()

                    Toast.makeText(
                        context,
                        result.message ?: "An unknown error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoadingIndicator() {
        binding.progreBar.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.progreBar.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Simpan query dan status fetching ke dalam Bundle
        outState.putString("lastQuery", currentQuery)
        outState.putBoolean("hasFetchedEvents", eventViewModel.hasFetchedUpcomingEvents)
    }
}

