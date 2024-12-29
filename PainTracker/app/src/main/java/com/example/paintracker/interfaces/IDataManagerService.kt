package com.example.paintracker.interfaces

import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.PainEntry
import com.example.paintracker.data.VisualiserLayer

interface IDataManagerService {
    fun listAllPainEntries(painCategories: List<PainCategory>): List<PainEntry>
}