package com.example.paintracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.INVISIBLE
import android.widget.LinearLayout.VISIBLE
import com.example.paintracker.databinding.FragmentRecordNotesBinding
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.Path
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class RecordNotesFragment : Fragment () {
    @Inject
    lateinit var pathService: IPathService

    var selectedDate: LocalDate = LocalDate.now()
        set(value) {
            field = value

            isDirty = false
            reflectIsDirty()

            loadNotes()
        }

    private var _binding: FragmentRecordNotesBinding? = null

    private val binding get() = _binding!!
    private var saveButton: FloatingActionButton? = null
    private var isDirty: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordNotesBinding.inflate(inflater, container, false)

        saveButton = _binding!!.saveButton

        saveButton!!.setOnClickListener {
            saveNotes()
            isDirty = false
            reflectIsDirty()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                isDirty = true
                reflectIsDirty()
            }
        })

        loadNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun reflectIsDirty() {
        saveButton!!.visibility = if (isDirty) VISIBLE else INVISIBLE
    }

    private fun loadNotes() {
        val dataRoot = pathService.getPath(Path.APPDATAROOT)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = selectedDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val notesPath = datePath.resolve("notes.txt")
        val notes = File(notesPath.toString())
        if (notes.exists()) {
            val fileContents = notes.readText()
            binding.notesEditText.setText(fileContents)
        }
        else {
            binding.notesEditText.setText("")
        }
    }

    private fun saveNotes() {
        val dataRoot = pathService.getPath(Path.APPDATAROOT)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = selectedDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val notesPath = datePath.resolve("notes.txt")
        val notesDirectory = datePath.toFile()
        if (!notesDirectory.exists()) {
            notesDirectory.mkdirs()
        }

        val notesContent = binding.notesEditText.text.toString()
        File(notesPath.toString()).writeText(notesContent)
    }
}