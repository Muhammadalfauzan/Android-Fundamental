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
import com.example.androidfundamental.ui.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentFavoriteBinding
import com.example.androidfundamental.di.Injection
import com.example.androidfundamental.ui.favorite.AdapterFavorite
import com.example.androidfundamental.ui.upcoming.EventViewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private  lateinit var adapterFavorite : AdapterFavorite

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(
            Injection.provideEventRepository(requireContext())
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        observeFavoriteEvents()
    }

    private fun setUpRecyclerView() {
        adapterFavorite = AdapterFavorite()
        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterFavorite
        }

        // Handle click to detail event
        adapterFavorite.setOnItemClickListener {  event->
            Toast.makeText(requireContext(),"Clicked: ${event.name}", Toast.LENGTH_SHORT).show()
            val bundle = Bundle().apply {
                putInt("event_id", event.id)
            }
            findNavController().navigate(R.id.action_favoriteFragment_to_detailFragment,bundle)
        }
    }

    private  fun observeFavoriteEvents(){
        eventViewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteList ->
            if (favoriteList.isNullOrEmpty()){
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.rvFavorite.visibility = View.GONE
            }else{
                binding.tvEmptyMessage.visibility = View.GONE
                binding.rvFavorite.visibility = View.VISIBLE
                adapterFavorite.submitList(favoriteList)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding= null
    }
}