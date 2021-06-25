package com.example.thorium.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.thorium.databinding.ItemPreferenceSpinnerBinding
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerAdapter
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerItem
import com.example.thorium.ui.customView.colorSpinner.ColorSpinnerView
import com.example.thorium.util.ColorUtils
import com.example.thorium.util.DataStoreManager


class PreferenceAdapter(
    val onPreferenceChange: OnPreferenceChange
) : ListAdapter<DataStoreManager.Preference, PreferenceViewHolder>(DiffUtil) {

    interface OnPreferenceChange {
        fun onPreferenceChange(newPreference: DataStoreManager.Preference)
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
    androidx.recyclerview.widget.DiffUtil.ItemCallback<DataStoreManager.Preference>() {
    override fun areItemsTheSame(
        oldItem: DataStoreManager.Preference,
        newItem: DataStoreManager.Preference
    ): Boolean {
        return true
    }

    override fun areContentsTheSame(
        oldItem: DataStoreManager.Preference,
        newItem: DataStoreManager.Preference
    ): Boolean {
        return oldItem == newItem
    }
}

class PreferenceViewHolder(
    private val binding: ItemPreferenceSpinnerBinding,
    onPreferenceChange: PreferenceAdapter.OnPreferenceChange
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var preference: DataStoreManager.Preference

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

    fun bind(preference: DataStoreManager.Preference) {
        this.preference = preference

        binding.apply {
            tvTitle.text = preference.title
            spinnerColor.selectItem(ColorSpinnerItem.mapFrom(preference.value))
        }
    }
}

