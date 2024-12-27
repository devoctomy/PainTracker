package com.example.paintracker.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.INVISIBLE
import android.widget.LinearLayout.VISIBLE
import android.widget.TextView
import com.example.paintracker.databinding.FragmentRecordNotesBinding
import com.example.paintracker.interfaces.IPainContext
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.data.PainContext
import com.example.paintracker.interfaces.INotesIoService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class RecordNotesFragment : Fragment () {
    // Dependency Injection
    @Inject lateinit var painContext: IPainContext
    @Inject lateinit var pathService: IPathService
    @Inject lateinit var notesIoService: INotesIoService

    private var _binding: FragmentRecordNotesBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("ViewBinding is only valid between onCreateView and onDestroyView.")

    private var painContextChangeListener: ((String, Any?, Any?) -> Unit)? = null
    private var notesTextWatcher: TextWatcher? = null

    // Control Refs
    private var saveButton: FloatingActionButton? = null
    private var dateLabel: TextView? = null
    private var isDirty: Boolean = false
    private var existingNotes: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordNotesBinding.inflate(inflater, container, false)

        saveButton = binding.saveButton
        dateLabel = binding.notesDateLabel

        saveButton!!.setOnClickListener {
            saveNotes()
            isDirty = false
            reflectIsDirty()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                isDirty = (existingNotes != s.toString())
                reflectIsDirty()
            }
        }
        binding.notesEditText.addTextChangedListener(notesTextWatcher)

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener = { propertyName, oldValue, newValue ->
            if (propertyName == "selectedDate") {
                loadNotes()
            }
        }
        painContext.addChangeListener(painContextChangeListener!!)

        loadNotes()
    }

    override fun onResume() {
        super.onResume()

        loadNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        notesTextWatcher?.let {
            binding.notesEditText.removeTextChangedListener(it)
        }

        val painContext: PainContext = painContext as PainContext
        painContextChangeListener?.let { listener ->
            painContext.removeChangeListener(listener)
        }

        _binding = null
        notesTextWatcher = null
        painContextChangeListener = null
    }

    private fun reflectIsDirty() {
        saveButton!!.visibility = if (isDirty) VISIBLE else INVISIBLE
    }

    private fun loadNotes() {
        if(_binding == null) {
            return
        }

        dateLabel!!.text = formatPainEntryDate(painContext.selectedDate)
        existingNotes = notesIoService.loadNotes(painContext.selectedDate)
        binding.notesEditText.setText(existingNotes)
    }

    private fun saveNotes() {
        val notesContent = binding.notesEditText.text.toString()
        notesIoService.saveNotes(painContext.selectedDate, notesContent)
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