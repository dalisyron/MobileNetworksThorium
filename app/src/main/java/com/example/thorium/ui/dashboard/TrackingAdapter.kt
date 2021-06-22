package com.example.thorium.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.entity.Tracking
import com.example.thorium.databinding.ItemTrackingBinding

typealias OnTrackingItemClicked = (Tracking) -> Unit

class TrackingAdapter(
    private val onTrackingItemClicked: OnTrackingItemClicked
) : ListAdapter<Tracking, TrackingViewHolder>(DiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding =
            ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackingViewHolder(binding, onTrackingItemClicked)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

internal object DiffUtil : DiffUtil.ItemCallback<Tracking>() {
    override fun areItemsTheSame(oldItem: Tracking, newItem: Tracking): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Tracking, newItem: Tracking): Boolean {
        return oldItem == newItem
    }
}

class TrackingViewHolder(
    private val binding: ItemTrackingBinding,
    private val onTrackingItemClicked: OnTrackingItemClicked
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onTrackingItemClicked(tracking)
        }
    }

    private lateinit var tracking: Tracking

    fun bind(tracking: Tracking) {
        this.tracking = tracking
        binding.apply {
            tvLogCount.text = tracking.cellLogs.size.toString()
            tvStartLocation.text = tracking.cellLogs.elementAtOrNull(0)?.toString() ?: "None"
            tvTrackingDate.text = "Date (TODO)"
        }
    }
}
