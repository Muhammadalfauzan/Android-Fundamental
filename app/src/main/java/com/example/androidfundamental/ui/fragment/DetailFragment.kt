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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.androidfundamental.utils.Injection
import com.example.androidfundamental.utils.NetworkResult
import com.example.androidfundamental.data.apimodel.ListEventsItem
import com.example.androidfundamental.data.apimodel.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentDetailBinding
import com.example.androidfundamental.ui.upcoming.EventViewModel


class DetailFragment : Fragment() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inisialisasi ViewModel
        val factory = ViewModelFactory(
            application = requireActivity().application,
            repository = Injection.provideEventRepository(),
            owner = this,
        )

        eventViewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]


        val eventId = arguments?.getInt("event_id") ?: 0
        if (eventId != 0) {
            eventViewModel.fetchEventDetail(eventId)
        } else {
            Log.e("DetailFragment", "Invalid event ID")
            Toast.makeText(context, "Invalid event ID", Toast.LENGTH_SHORT).show()
            return
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        eventViewModel.eventDetail.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    // Tampilkan indikator loading
                    showShimmerDetail()
                }

                is NetworkResult.Success -> {
                    // Tampilkan detail event yang berhasil di-fetch
                    result.data?.let { event ->
                        displayEventDetail(event)
                    }
                    hideShimmerDetail()
                }

                is NetworkResult.Error -> {
                    // Tampilkan pesan error
                    hideShimmerDetail()
                    showError(result.message)
                }
            }
        }
    }

    private fun displayEventDetail(event: ListEventsItem) {
        // Menampilkan detail event ke UI
        binding.tvNamaAcara.text = event.name
        val htmlDescription =
            HtmlCompat.fromHtml(event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvInformasi.text = htmlDescription
        Glide.with(this).load(event.imageLogo).into(binding.imgLogoDetail)

        // Tampilkan informasi lain seperti owner, time, city, dll
        binding.tvDiselengara.text = event.ownerName
        binding.tvSisaKouta.text = event.quota.toString()
        binding.tvWaktuAcara.text = event.endTime

        binding.tvSisaKouta.text = event.quota.toString()
        binding.tvWaktuAcara.text = event.endTime

        // Set listener untuk button Register
        binding.btnRegist.setOnClickListener {
            // Pastikan link untuk registrasi tersedia
            event.link?.let { url ->
                // Intent untuk membuka URL di browser
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } ?: run {
                // Jika tidak ada link, tampilkan pesan error
                Toast.makeText(context, "Link registrasi tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*    private fun showLoadingIndicator() {
            binding.progresbar.visibility = View.VISIBLE
        }

        private fun hideLoadingIndicator() {
            binding.progresbar.visibility = View.GONE
        }*/

    private fun showShimmerDetail() {
        binding.shimmerDetail.visibility = View.VISIBLE
        binding.shimmerDetail.startShimmer()
        binding.imgLogoDetail.visibility = View.GONE
        binding.btnRegist.visibility = View.GONE
        binding.tvNamaAcara.visibility = View.GONE
        binding.lbPenyelenggara.visibility = View.GONE
        binding.tvDiselengara.visibility = View.GONE
        binding.lbSisaKouta.visibility = View.GONE
        binding.tvSisaKouta.visibility = View.GONE
        binding.tvWaktuAcara.visibility = View.GONE
        binding.textView3.visibility = View.GONE
        binding.tvInformasi.visibility = View.GONE
    }

    private fun hideShimmerDetail() {
        binding.shimmerDetail.stopShimmer()
        binding.shimmerDetail.visibility = View.GONE
        binding.imgLogoDetail.visibility = View.VISIBLE
        binding.btnRegist.visibility = View.VISIBLE
        binding.tvNamaAcara.visibility = View.VISIBLE
        binding.lbPenyelenggara.visibility = View.VISIBLE
        binding.tvDiselengara.visibility = View.VISIBLE
        binding.lbSisaKouta.visibility = View.VISIBLE
        binding.tvSisaKouta.visibility = View.VISIBLE
        binding.tvWaktuAcara.visibility = View.VISIBLE
        binding.textView3.visibility = View.VISIBLE
        binding.tvInformasi.visibility = View.VISIBLE

    }

    private fun showError(message: String?) {
        Toast.makeText(context, message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
    }
}

