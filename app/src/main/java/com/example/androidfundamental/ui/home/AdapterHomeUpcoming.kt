package com.example.androidfundamental.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfundamental.data.apimodel.ListEventsItem
import com.example.androidfundamental.databinding.ItemUpcomingHomeBinding
import com.example.androidfundamental.databinding.ShimmerUpcomingBinding

@SuppressLint("NotifyDataSetChanged")
class AdapterHomeUpcoming : ListAdapter<ListEventsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((ListEventsItem) -> Unit)? = null
    private var isLoading = true
    private var shimmerItemCount = 3

    // ViewHolder untuk event menggunakan View Binding
    inner class ListViewHolder(private val binding: ItemUpcomingHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ListEventsItem?) {
            if (data != null) {
                binding.judulEventHome.text = data.name
                Glide.with(binding.root.context)
                    .load(data.imageLogo)
                    .fitCenter()
                    .into(binding.imgLogoHome)

                // Listener untuk klik item
                binding.root.setOnClickListener {
                    onItemClickListener?.invoke(data)
                }
            }
        }
    }

    // ViewHolder untuk shimmer menggunakan View Binding
    inner class ShimmerViewHolder(binding: ShimmerUpcomingBinding) : RecyclerView.ViewHolder(binding.root) {
        val shimmerFrameLayout: com.facebook.shimmer.ShimmerFrameLayout = binding.shimmerUpcoming
    }

    // Fungsi untuk menampilkan shimmer effect

    fun showShimmerEffect() {
        isLoading = true
        notifyDataSetChanged() // Refresh adapter untuk memulai loading
    }

    // Fungsi untuk menyembunyikan shimmer effect
    fun hideShimmerEffect() {
        isLoading = false
        notifyDataSetChanged() // Refresh adapter untuk menampilkan data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LOADING) {
            val binding = ShimmerUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ShimmerViewHolder(binding)
        } else {
            val binding = ItemUpcomingHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ListViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isLoading) {
            if (holder is ShimmerViewHolder) {
                holder.shimmerFrameLayout.startShimmer()
            }
        } else {
            val event = getItem(position) // Mengambil item dengan aman
            if (holder is ListViewHolder) {
                holder.bind(event)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) {
            shimmerItemCount
        } else {
            super.getItemCount()
        }
    }

    // Menentukan ViewType (shimmer atau normal)
    override fun getItemViewType(position: Int): Int {
        return if (isLoading) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    // Fungsi untuk mengatur listener klik
    fun setOnItemClickListener(listener: (ListEventsItem) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_NORMAL = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem) =
                oldItem == newItem
        }
    }
}