package com.example.androidfundamental.ui.upcoming

import android.annotation.SuppressLint
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
import com.example.androidfundamental.data.remote.response.ListEventsItem

@SuppressLint("NotifyDataSetChanged")
class AdapterUpcoming : ListAdapter<ListEventsItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((ListEventsItem) -> Unit)? = null
    private var isLoading = true
    private var shimmerItemCount = 10

    class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shimmerFrameLayout: com.facebook.shimmer.ShimmerFrameLayout =
            itemView.findViewById(R.id.shimmerFrame)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageEvent: ImageView = itemView.findViewById(R.id.imgLogo)
        private val eventName: TextView = itemView.findViewById(R.id.judulEvent)

        fun bind(data: ListEventsItem?) {
            if (data != null) {
                eventName.text = data.name
                Glide.with(itemView.context)
                    .load(data.imageLogo)
                    .fitCenter()
                    .into(imageEvent)

                itemView.setOnClickListener {
                    onItemClickListener?.invoke(data)
                }
            }
        }
    }

    fun showShimmerEffect() {
        isLoading = true
        notifyDataSetChanged() // Refresh adapter untuk memulai loading
    }

    fun hideShimmerEffect() {
        isLoading = false
        notifyDataSetChanged() // Refresh adapter untuk menampilkan data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.shimmer_loading, parent, false)
            ShimmerViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_upcoming, parent, false)
            ListViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isLoading) {
            if (holder is ShimmerViewHolder) {
                holder.shimmerFrameLayout.startShimmer()
            }
        } else {
            if (holder is ListViewHolder) {
                getItem(position)?.let { holder.bind(it) }
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

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
    }

    fun setOnItemClickListener(listener: (ListEventsItem) -> Unit) {
        this.onItemClickListener = listener
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
