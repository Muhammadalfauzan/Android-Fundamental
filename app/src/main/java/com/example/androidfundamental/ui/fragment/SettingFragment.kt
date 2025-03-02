package com.example.androidfundamental.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider

import com.example.androidfundamental.ui.ViewModelFactory
import com.example.androidfundamental.databinding.FragmentSettingBinding
import com.example.androidfundamental.di.Injection
import com.example.androidfundamental.utils.SettingPreferences
import com.example.androidfundamental.utils.datastore
import com.example.androidfundamental.ui.settings.SettingsViewModel


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SettingPreferences dari dataStore
        val pref = SettingPreferences.getInstance(requireContext().datastore)

        // Gunakan ViewModelFactory khusus untuk SettingsViewModel
        val viewModelFactory = ViewModelFactory.getInstance(
            repository = Injection.provideEventRepository(requireContext()),
            preferences = pref
        )

        val settingsViewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        // Observe perubahan tema
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchTheme.isChecked = isDarkModeActive
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Simpan perubahan saat switch diubah
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

