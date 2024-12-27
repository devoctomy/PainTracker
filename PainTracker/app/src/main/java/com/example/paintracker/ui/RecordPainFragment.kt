package com.example.paintracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.paintracker.databinding.FragmentRecordPainBinding
import com.example.paintracker.interfaces.IPainContext
import com.example.paintracker.data.PainContext
import com.example.paintracker.ui.widgets.PainVisualiser
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class RecordPainFragment : Fragment() {
    @Inject lateinit var painContext: IPainContext

    private var _binding: FragmentRecordPainBinding? = null
    private var painContextChangeListener: ((String, Any?, Any?) -> Unit)? = null

    private val binding get() = _binding!!
    var painVisualiser: PainVisualiser? = null
    var dateLabel: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecordPainBinding.inflate(inflater, container, false)

        painVisualiser = binding.painVisualiser
        dateLabel = binding.painDateLabel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener = { propertyName, oldValue, newValue ->
            if (propertyName == "selectedDate") {
                dateLabel!!.text = formatPainEntryDate(newValue as LocalDate)
                painVisualiser!!.selectedDate = newValue
            }
        }
        painContext.addChangeListener(painContextChangeListener!!)
    }

    override fun onResume() {
        super.onResume()

        painVisualiser!!.selectedDate = painContext.selectedDate
        dateLabel!!.text = formatPainEntryDate(painContext.selectedDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener?.let { listener ->
            painContext.removeChangeListener(listener)
        }

        _binding = null
        painContextChangeListener = null
    }

    private fun formatPainEntryDate(date: LocalDate): String {
        val day = date.dayOfMonth
        val ordinal = getOrdinal(day)
        val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        return "$day$ordinal $monthYear"
    }

    private fun getOrdinal(day: Int): String {
        return when {
            day % 100 in 11..13 -> "th" // Special case for 11th, 12th, 13th
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }
}