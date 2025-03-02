package com.example.androidfundamental.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfundamental.data.local.entity.FavoriteEvent
import com.example.androidfundamental.R

class AdapterFavorite : ListAdapter<FavoriteEvent, AdapterFavorite.ListViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((FavoriteEvent) -> Unit)? = null

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageEvent: ImageView = itemView.findViewById(R.id.imgLogo)
        private val eventName: TextView = itemView.findViewById(R.id.judulEvent)

        fun bind(data: FavoriteEvent) {
            eventName.text = data.name
            Glide.with(itemView.context)
                .load(data.mediaCover)
                .fitCenter()
                .into(imageEvent)

            itemView.setOnClickListener {
                onItemClickListener?.invoke(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    fun setOnItemClickListener(listener: (FavoriteEvent) -> Unit) {
        this.onItemClickListener = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEvent>() {
            override fun areItemsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoriteEvent, newItem: FavoriteEvent) =
                oldItem == newItem
        }
    }
}
