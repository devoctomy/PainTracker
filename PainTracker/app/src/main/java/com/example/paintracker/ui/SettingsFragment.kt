package com.example.paintracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.paintracker.R
import com.example.paintracker.data.Sex
import com.example.paintracker.databinding.FragmentSettingsBinding
import com.example.paintracker.interfaces.IConfigService
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    // Dependency Injection
    @Inject
    lateinit var configService: IConfigService

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Control Refs
    private lateinit var patientNameEditText: TextInputEditText
    private lateinit var patientSexSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        patientNameEditText = binding.patientNameEditText
        patientSexSpinner = binding.patientSexSpinner

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the dropdown for Patient Sex
        val patientSexOptions = resources.getStringArray(R.array.patient_sex_options)
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            patientSexOptions
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.patientSexSpinner.adapter = spinnerAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSettings() {
        val config = configService.getCurrent()

        patientNameEditText.setText(config.patientName)
        patientSexSpinner.setSelection(config.patientSex.ordinal)
    }

    private fun saveSettings() {
        val config = configService.getCurrent()
        config.patientName = patientNameEditText.text.toString()
        config.patientSex = Sex.valueOf(binding.patientSexSpinner.selectedItem as String)
        configService.saveCurrent(requireContext())
    }
}