package com.example.thorium.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thorium.databinding.FragmentSettingsBinding
import com.example.thorium.util.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.rvList


@AndroidEntryPoint
class SettingsFragment : Fragment(), PreferenceAdapter.OnPreferenceChange {

    private var adapter: PreferenceAdapter? = null
    private var _binding: FragmentSettingsBinding? = null
    private val settingsViewModel: SettingsViewModel by viewModels()
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
    }

    private fun init() {
        adapter = PreferenceAdapter(this)

        rvList.layoutManager = LinearLayoutManager(requireContext())
        rvList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPreferenceChange(newPreference: DataStoreManager.Preference) {

    }

    companion object {
        const val TAG = "SettingsFragment"
        fun getInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}