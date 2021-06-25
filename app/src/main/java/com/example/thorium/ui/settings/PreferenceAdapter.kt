package com.example.thorium.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.entity.Preference
import com.example.thorium.databinding.ItemPreferenceSpinnerBinding
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerAdapter
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerView
import com.example.thorium.util.ColorUtils


class PreferenceAdapter(
    val onPreferenceChange: OnPreferenceChange
) : ListAdapter<Preference, PreferenceViewHolder>(DiffUtil) {

    interface OnPreferenceChange {
        fun onPreferenceChange(newPreference: Preference)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val binding =
            ItemPreferenceSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreferenceViewHolder(binding,onPreferenceChange)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

internal object DiffUtil :
    androidx.recyclerview.widget.DiffUtil.ItemCallback<Preference>() {
    override fun areItemsTheSame(
        oldItem: Preference,
        newItem: Preference
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: Preference,
        newItem: Preference
    ): Boolean {
        return oldItem == newItem
    }
}

class PreferenceViewHolder(
    private val binding: ItemPreferenceSpinnerBinding,
    onPreferenceChange: PreferenceAdapter.OnPreferenceChange
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var preference: Preference

    init {
        ColorSpinnerAdapter().apply {
            setItems(ColorUtils.colorsList)
        }.also {
            binding.spinnerColor.setAdapter(it)
        }

        binding.spinnerColor.setOnItemChangeListener(object :
            ColorSpinnerView.OnItemChangeListener {
            override fun onChange(colorSpinnerItem: ColorSpinnerItem) {
                preference.apply {
                    value = colorSpinnerItem.color
                }.also {
                    onPreferenceChange.onPreferenceChange(it)
                }
            }
        })
    }

    fun bind(preference: Preference) {
        this.preference = preference

        binding.apply {
            tvTitle.text = preference.title
            spinnerColor.selectItem(ColorSpinnerItem.mapFrom(preference.value))
        }
    }
}

