package com.example.thorium.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.entity.Tracking
import com.example.thorium.R
import com.example.thorium.databinding.ItemTrackingBinding
import com.example.thorium.util.getFormattedDate
import java.text.SimpleDateFormat
import java.util.*

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

            tvTrackingDate.text = itemView.context.getFormattedDate(tracking.dateCreated)

            tvLogCount.text =
                itemView.context.getString(R.string.log_count_place_holder, tracking.cellLogs.size)

            tvStartLocation.text = itemView.context.getString(
                R.string.start_location_place_holder,
                tracking.startLocation.toFormattedString()
            )

            tvStopLocation.text = itemView.context.getString(
                R.string.stop_location_place_holder,
                tracking.endLocation?.toFormattedString() ?: ""
            )
        }
    }
}
