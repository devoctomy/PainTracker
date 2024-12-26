package com.example.paintracker.data

import java.time.LocalDate

data class PainEntry (
    val date: LocalDate,
    val layers: List<VisualiserLayer>,
    var hasNotes: Boolean
)