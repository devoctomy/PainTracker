package com.example.paintracker.services

import com.example.paintracker.interfaces.INotesIoService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.Path
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NotesIoService @Inject constructor(
    private val pathService: IPathService
)  : INotesIoService {

    override fun loadNotes(date: LocalDate): String? {
        val dataRoot = pathService.getPath(Path.APPDATAROOT)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = date.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val notesPath = datePath.resolve("notes.txt")
        val notes = File(notesPath.toString())
        if (notes.exists()) {
            val fileContents = notes.readText()
            return fileContents
        }
        else {
            return null
        }
    }

    override fun saveNotes(date: LocalDate, notes: String) {
        val dataRoot = pathService.getPath(Path.APPDATAROOT)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = date.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val notesPath = datePath.resolve("notes.txt")
        val notesDirectory = datePath.toFile()
        if (!notesDirectory.exists()) {
            notesDirectory.mkdirs()
        }

        File(notesPath.toString()).writeText(notes)
    }

}