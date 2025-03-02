package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental.R
import com.example.androidfundamental.data.Resource
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.example.androidfundamental.ui.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentUpcomingBinding
import com.example.androidfundamental.di.Injection

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(
            Injection.provideEventRepository(requireContext())
        )
    }

    private lateinit var adapter: AdapterUpcoming
    private var currentQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            currentQuery = it.getString("lastQuery")
        }

        setupRecyclerView()
        observeViewModel()

        adapter.setOnItemClickListener { data ->
            val eventId: Int = data.id
            val bundle = Bundle().apply {
                putInt("event_id", eventId)
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
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoadingIndicator()
                    adapter.showShimmerEffect()
                }

                is Resource.Success -> {
                    adapter.hideShimmerEffect()
                    hideLoadingIndicator()

                    val listEvents = result.data
                    if (listEvents.isNotEmpty()) {
                        adapter.submitList(listEvents)
                    } else {
                        adapter.submitList(emptyList())
                        Toast.makeText(context, "No events found", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    adapter.hideShimmerEffect()
                    hideLoadingIndicator()
                    Toast.makeText(
                        context,
                        result.message,
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
        outState.putString("lastQuery", currentQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}