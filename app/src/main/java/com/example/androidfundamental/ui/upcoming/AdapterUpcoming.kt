package com.example.androidfundamental.ui.upcoming

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfundamental.R
import com.example.androidfundamental.data.apimodel.ListEventsItem

class AdapterUpcoming : ListAdapter<ListEventsItem, AdapterUpcoming.ListViewHolder>(DIFF_CALLBACK) {

    private var onItemClickAdapter: OnItemClickAdapter? = null

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageEvent: ImageView = itemView.findViewById(R.id.imgLogo)
        private val eventName: TextView = itemView.findViewById(R.id.judulEvent)
        //private val eventDate: TextView = itemView.findViewById(R.id.tvEventDate)

        fun bind(data: ListEventsItem) {
            // Menggunakan Glide untuk memuat gambar dari URL
            Glide.with(itemView.context)
                .load(data.imageLogo) // URL dari gambar
                .into(imageEvent)

            // Mengatur teks untuk nama event dan tanggal event
            eventName.text = data.name
          //  eventDate.text = data.beginTime

            // Mengatur listener untuk klik item
            itemView.setOnClickListener {
                onItemClickAdapter?.onItemClick(data)
            }
        }
    }

    // Set listener untuk klik item
    fun setOnItemClickListener(listener: OnItemClickAdapter) {
        this.onItemClickAdapter = listener
    }

    // Inflate layout item dan buat ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming, parent, false)
        return ListViewHolder(view)
    }

    // Menghubungkan data dengan ViewHolder
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val event = getItem(position)
        if (event != null) {
            Log.d("AdapterUpcoming", "Binding item at position: $position, event: ${event.name}")
            holder.bind(event)
        } else {
            Log.e("AdapterUpcoming", "Event at position $position is null")
        }
    }

    // Interface untuk menangani klik item
    interface OnItemClickAdapter {
        fun onItemClick(data: ListEventsItem)
    }

    // DiffUtil untuk optimasi pembaruan data
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                // Perbandingan berdasarkan ID event
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                // Periksa apakah semua konten sama
                return oldItem == newItem
            }
        }
    }
}
