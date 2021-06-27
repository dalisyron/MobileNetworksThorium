package com.example.thorium.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.common.entity.Preference
import com.example.thorium.R
import com.example.thorium.databinding.FragmentSettingsBinding
import com.example.thorium.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException
import kotlinx.android.synthetic.main.fragment_settings.npAutoLogTimer
import kotlinx.android.synthetic.main.fragment_settings.rvList


@AndroidEntryPoint
class SettingsFragment : Fragment(), PreferenceAdapter.OnPreferenceChange {

    private lateinit var timeToIndex: Map<Int, Int>
    private lateinit var indexToTime: Map<Int, Int>

    private var adapter: PreferenceAdapter? = null
    private var _binding: FragmentSettingsBinding? = null
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val parentViewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observe()
    }

    private fun observe() {
        settingsViewModel.preferenceList.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
        })

        parentViewModel.timer.observe(viewLifecycleOwner, { time ->
            binding.npAutoLogTimer.value = timeToIndex[time]!!
        })
    }

    private fun init() {
        adapter = PreferenceAdapter(this)

        rvList.layoutManager = LinearLayoutManager(requireContext())
        rvList.adapter = adapter

        setupNumberPicker(R.array.times)
        npAutoLogTimer.setOnValueChangedListener { picker, oldVal, newVal ->
            if (newVal != oldVal) {
                parentViewModel.onTimerChanged(indexToTime[newVal]!!)
            }
        }
    }

    private fun setupNumberPicker(@ArrayRes config: Int) {
        val displayedValues = resources.getStringArray(config)
        npAutoLogTimer.displayedValues = displayedValues
        npAutoLogTimer.minValue = 0
        npAutoLogTimer.maxValue = displayedValues.size - 1

        indexToTime = displayedValues.mapIndexed { index, s ->
            val timeValue = when (s) {
                getString(R.string.time_5s) -> 5
                getString(R.string.time_10s) -> 10
                getString(R.string.time_30s) -> 30
                getString(R.string.time_1m) -> 60
                getString(R.string.time_2m) -> 2 * 60
                getString(R.string.time_5m) -> 5 * 60
                getString(R.string.time_10m) -> 10 * 60
                getString(R.string.time_30m) -> 30 * 60
                getString(R.string.time_1h) -> 60 * 60
                else -> throw IllegalArgumentException("Invalid time string in config $s")
            }
            index to timeValue
        }.toMap()

        timeToIndex = indexToTime.entries.associate { (k, v) -> v to k }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPreferenceChange(newPreference: Preference) {
        settingsViewModel.setPreference(newPreference)
    }

    companion object {
        const val TAG = "SettingsFragment"
        fun getInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}