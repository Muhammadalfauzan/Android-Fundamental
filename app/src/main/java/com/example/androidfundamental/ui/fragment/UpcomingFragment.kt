package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfundamental.ui.upcoming.AdapterUpcoming
import com.example.androidfundamental.ui.upcoming.EventViewModel
import com.example.androidfundamental.R
import com.example.androidfundamental.data.apimodel.ListEventsItem
import com.example.androidfundamental.data.apimodel.ViewModelFactory


class UpcomingFragment : Fragment() {

    // ViewModel untuk mendapatkan data event
    private lateinit var eventViewModel: EventViewModel

    // Adapter untuk RecyclerView
    private lateinit var adapter: AdapterUpcoming

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel dengan Factory
        val factory = ViewModelFactory(requireContext()) // Buat instance dari ViewModelFactory
        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        // Inisialisasi RecyclerView dan Adapter
        adapter = AdapterUpcoming()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvUpcoming)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Observasi data dari ViewModel
        eventViewModel.events.observe(viewLifecycleOwner) { events ->
            Log.d("UpcomingFragment", "Events received from ViewModel: ${events?.size}")
            if (!events.isNullOrEmpty()) {
                Log.d("UpcomingFragment", "First event: ${events[0]?.name}")
            }
            adapter.submitList(events)
        }

        // Observasi jika ada error dari ViewModel
        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Panggil fungsi untuk fetch data dari ViewModel
        eventViewModel.fetchEvents()

        // Set click listener pada adapter
        adapter.setOnItemClickListener(object : AdapterUpcoming.OnItemClickAdapter {
            override fun onItemClick(data: ListEventsItem) {
                // Handle click event (contohnya menampilkan Toast atau navigasi ke detail)
                Toast.makeText(context, "Clicked: ${data.name}", Toast.LENGTH_SHORT).show()
                // Anda bisa melakukan navigasi ke Fragment/Activity lain jika diperlukan
            }
        })
    }
}
