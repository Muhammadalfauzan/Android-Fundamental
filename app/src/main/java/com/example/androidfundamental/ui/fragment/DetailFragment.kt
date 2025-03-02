package com.example.androidfundamental.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.androidfundamental.data.local.entity.FavoriteEvent
import com.example.androidfundamental.R
import com.example.androidfundamental.data.Resource
import com.example.androidfundamental.di.Injection
import com.example.androidfundamental.data.remote.response.ListEventsItem
import com.example.androidfundamental.ui.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentDetailBinding
import com.example.androidfundamental.ui.upcoming.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var isFavorite = false
    private var eventDetail: ListEventsItem? = null

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(
            repository = Injection.provideEventRepository(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("event_id") ?: -1
        if (eventId > 0) {
            observeEventDetail(eventId)
            observeFavoriteStatus(eventId)
        } else {
            Log.e("DetailFragment", "Invalid event ID")
            Toast.makeText(requireContext(), "Invalid event ID", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun observeEventDetail(eventId: Int) {
        eventViewModel.getEventDetail(eventId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> showShimmerDetail()
                is Resource.Success -> {
                    result.data?.let { event ->
                        eventDetail = event
                        displayEventDetail(event)
                        setupFavoriteButton()
                    } ?: showError("Data tidak tersedia")
                    hideShimmerDetail()
                }

                is Resource.Error -> {
                    hideShimmerDetail()
                    showError(result.message)
                }
            }
        }
    }

    private fun observeFavoriteStatus(eventId: Int) {
        lifecycleScope.launch {
            eventViewModel.isFavorite.collect { isFav ->
                isFavorite = isFav
                updateFavoriteButton()
            }
        }
        eventViewModel.checkFavoriteStatus(eventId)
    }

    private fun setupFavoriteButton() {
        binding.btnFav.setOnClickListener {
            eventDetail?.let { event ->
                val favoriteEvent = FavoriteEvent(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover,
                    isFavorite = !isFavorite
                )

                if (isFavorite) {
                    eventViewModel.removeFromFavorite(event.id)
                    Toast.makeText(requireContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    eventViewModel.addToFavorite(favoriteEvent)
                    Toast.makeText(requireContext(), "Ditambahkan ke favorit", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateFavoriteButton() {
        val iconRes = if (isFavorite) R.drawable.ic_favorit else R.drawable.ic_favorite_border
        binding.btnFav.setImageResource(iconRes)
    }

    private fun displayEventDetail(event: ListEventsItem) {
        Glide.with(this).load(event.imageLogo).into(binding.imgLogoDetail)
        binding.tvDiselengara.text = event.ownerName

        val htmlDescription =
            HtmlCompat.fromHtml(event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvInformasi.text = htmlDescription

        val qouta = event.quota ?: 0
        val registrants = event.registrants ?: 0
        val sisaKouta = (qouta - registrants).coerceAtLeast(0)

        binding.tvSisaKouta.text = sisaKouta.toString()
        binding.tvNamaAcara.text = event.name
        binding.tvWaktuAcara.text = formatDateTime(event.beginTime, event.endTime)
        binding.btnRegist.setOnClickListener {
            event.link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } ?: run {
                Toast.makeText(context, "Link registrasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatDateTime(beginTime: String?, endTime: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

        return try {
            val beginDate = beginTime?.let { inputFormat.parse(it) }
            val endDate = endTime?.let { inputFormat.parse(it) }
            "${beginDate?.let { outputFormat.format(it) }} - ${endDate?.let { outputFormat.format(it) }}"
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }

    private fun showShimmerDetail() {
        binding.shimmerDetail.apply {
            visibility = View.VISIBLE
            startShimmer()
        }

        listOf(
            binding.imgLogoDetail,
            binding.btnRegist,
            binding.tvNamaAcara,
            binding.lbPenyelenggara,
            binding.tvDiselengara,
            binding.lbSisaKouta,
            binding.tvSisaKouta,
            binding.tvWaktuAcara,
            binding.textView3,
            binding.tvInformasi
        ).forEach { it.visibility = View.GONE }
    }

    private fun hideShimmerDetail() {
        binding.shimmerDetail.apply {
            stopShimmer()
            visibility = View.GONE
        }

        listOf(
            binding.imgLogoDetail,
            binding.btnRegist,
            binding.tvNamaAcara,
            binding.lbPenyelenggara,
            binding.tvDiselengara,
            binding.lbSisaKouta,
            binding.tvSisaKouta,
            binding.tvWaktuAcara,
            binding.textView3,
            binding.tvInformasi
        ).forEach { it.visibility = View.VISIBLE }
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
