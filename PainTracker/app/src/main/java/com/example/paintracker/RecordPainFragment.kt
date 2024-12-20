package com.example.paintracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.paintracker.databinding.FragmentRecordPainBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RecordPainFragment : Fragment() {

    private var _binding: FragmentRecordPainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecordPainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val painVisualiser = binding.painVisualiser
        painVisualiser.dataRoot = PathManager.appDataDir
        painVisualiser.painCategories = ConfigManager.getCurrent().painCategories
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_binding = null
    }
}