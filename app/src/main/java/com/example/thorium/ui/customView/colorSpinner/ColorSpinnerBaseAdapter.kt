package com.example.thorium.ui.customView.colorSpinner

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class ColorSpinnerBaseAdapter :
    ListAdapter<ColorSpinnerItem, RecyclerView.ViewHolder>(DiffUtil) {
    lateinit var onItemSelectedListener: OnItemSelectedListener

    fun setItems(items: List<ColorSpinnerItem>) {
        submitList(items)
    }

    fun getItemForBindView(position: Int): ColorSpinnerItem {
        return currentList[position]
    }

    fun getPosition(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun setListener(onItemSelectedListener: OnItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    interface OnItemSelectedListener {
        fun onItemSelected(colorSpinnerItem: ColorSpinnerItem, position: Int)
    }
}

internal object DiffUtil :
    androidx.recyclerview.widget.DiffUtil.ItemCallback<ColorSpinnerItem>() {
    override fun areItemsTheSame(
        oldItem: ColorSpinnerItem,
        newItem: ColorSpinnerItem
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: ColorSpinnerItem,
        newItem: ColorSpinnerItem
    ): Boolean {
        return oldItem == newItem
    }
}