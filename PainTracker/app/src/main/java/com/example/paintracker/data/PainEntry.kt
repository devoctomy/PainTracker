package com.example.paintracker.data

import java.time.LocalDate

data class PainEntry (
    val date: LocalDate,
    val painCategories: List<PainCategory>,
    var hasNotes: Boolean
)