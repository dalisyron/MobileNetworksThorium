package com.example.thorium.ui.customView.colorSpinner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.thorium.databinding.ItemColorSpinnerBinding

class ColorSpinnerAdapter : ColorSpinnerBaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemColorSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, onItemSelectedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.setData(getItemForBindView(position), getPosition(position))
        }
    }

    class ItemViewHolder(
        private val binding: ItemColorSpinnerBinding,
        listener: OnItemSelectedListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private lateinit var item: ColorSpinnerItem
        private var itemPosition = 0
        private var onItemSelectedListener: OnItemSelectedListener = listener

        init {
            binding.root.setOnClickListener(this@ItemViewHolder)
        }

        fun setData(item: ColorSpinnerItem, position: Int) {
            this.item = item
            this.itemPosition = position

            binding.apply {
                tvName.text = item.name
                vColor.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, item.color)
            }
        }

        override fun onClick(v: View?) {
            onItemSelectedListener.onItemSelected(item, itemPosition)
        }
    }
}
