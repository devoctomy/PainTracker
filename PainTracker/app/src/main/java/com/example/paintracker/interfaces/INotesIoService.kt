package com.example.paintracker.interfaces

import java.time.LocalDate

interface INotesIoService {
    fun loadNotes(date: LocalDate): String?
    fun saveNotes(date: LocalDate, notes: String)
}