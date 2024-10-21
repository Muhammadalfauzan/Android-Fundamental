package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidfundamental.Injection
import com.example.androidfundamental.NetworkResult
import com.example.androidfundamental.R
import com.example.androidfundamental.data.apimodel.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentFinishedBinding
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.google.android.material.search.SearchView



class FinishedFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: AdapterUpcoming
    private lateinit var binding: FragmentFinishedBinding
    private var currentQuery: String? = null
    private var scrollState: IntArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        Log.d("FinishedFragment", "onCreateView() called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FinishedFragment", "onViewCreated() called")

        val factory = ViewModelFactory(
            application = requireActivity().application,
            repository = Injection.provideEventRepository(),
            owner = this
        )

        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        observeViewModel()

        // Jika data sudah pernah di-fetch, tidak fetch ulang
        if (!eventViewModel.hasFetchedFinishedEvents) {
            eventViewModel.fetchFinishedEvents()
        }


        // Handle item click pada event
        adapter.setOnItemClickListener { data ->
            val bundle = Bundle().apply {
                putInt("event_id", data.id ?: 0) // Kirim hanya ID event ke DetailFragment
            }
            findNavController().navigate(R.id.action_finishedFragment_to_detailFragment, bundle)
        }
    }

    // Setup RecyclerView
    private fun setupRecyclerView() {
        adapter = AdapterUpcoming()
        binding.rvFinished.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvFinished.adapter = adapter
        adapter.showShimmerEffect() // Tampilkan shimmer saat loading
    }

    // Setup Search Functionality
    private fun setupSearch() {
        // Tampilkan SearchView ketika SearchBar diklik
        binding.searchBar.setOnClickListener {
            binding.searchView.show() // Show the Material SearchView
        }

        // Handle query submit dari keyboard
        binding.searchView.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { // Deteksi action "search"
                val query = binding.searchView.text.toString().trim() // Ambil query
                if (query.isNotBlank()) {
                    currentQuery = query // Simpan query saat ini
                    // Fetch event berdasarkan query yang dimasukkan oleh user
                    eventViewModel.fetchFinishedEvents(query)
                } else {
                    Toast.makeText(context, "Please enter a search term", Toast.LENGTH_SHORT).show()
                }
                binding.searchView.hide() // Sembunyikan SearchView setelah submit
                true
            } else {
                false
            }
        }

        // Handle saat SearchView di-close
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN && binding.searchView.text.isNullOrBlank()) {
                currentQuery = null // Reset query
                // Fetch ulang event tanpa query jika pencarian dikosongkan
                eventViewModel.fetchFinishedEvents(null)
            }
        }
    }
    // Menyimpan posisi scroll pada RecyclerView
    private fun saveScrollPosition() {
        val layoutManager = binding.rvFinished.layoutManager as StaggeredGridLayoutManager
        scrollState = layoutManager.findFirstVisibleItemPositions(null)
    }

    // Mengembalikan posisi scroll yang disimpan sebelumnya
    private fun restoreScrollPosition() {
        scrollState?.let {
            val layoutManager = binding.rvFinished.layoutManager as StaggeredGridLayoutManager
            layoutManager.scrollToPositionWithOffset(it[0], 0)
        }
    }

    // Mengamati perubahan LiveData dari ViewModel dan menampilkan data di RecyclerView
    private fun observeViewModel() {
        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    saveScrollPosition()
                    adapter.showShimmerEffect() // Tampilkan efek shimmer saat loading
                }

                is NetworkResult.Success -> {
                    adapter.hideShimmerEffect() // Hide shimmer setelah data siap
                    result.data?.let { events ->
                        if (events.isNotEmpty()) {
                            adapter.submitList(events) // Tampilkan events di RecyclerView
                            restoreScrollPosition()
                        } else {
                            adapter.submitList(emptyList()) // Kosongkan list jika tidak ada event
                        }
                    }
                }

                is NetworkResult.Error -> {
                    adapter.hideShimmerEffect() // Hide shimmer jika terjadi error
                    adapter.submitList(emptyList()) // Kosongkan list pada error
                    Toast.makeText(context, result.message ?: "An unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
