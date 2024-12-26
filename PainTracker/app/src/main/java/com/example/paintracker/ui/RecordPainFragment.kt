package com.example.paintracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.paintracker.databinding.FragmentRecordPainBinding
import com.example.paintracker.interfaces.IPainContext
import com.example.paintracker.data.PainContext
import com.example.paintracker.ui.widgets.PainVisualiser
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class RecordPainFragment : Fragment() {
    @Inject lateinit var painContext: IPainContext

    var painVisualiser: PainVisualiser? = null

    private var _binding: FragmentRecordPainBinding? = null

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

        painVisualiser = _binding!!.painVisualiser

        val painContext: PainContext = painContext as PainContext
        painContext.addChangeListener { propertyName, oldValue, newValue ->
            if(propertyName == "selectedDate") {
                painVisualiser!!.selectedDate = newValue as LocalDate
            }
        }
    }

    override fun onResume() {
        super.onResume()

        painVisualiser!!.selectedDate = painContext.selectedDate
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}