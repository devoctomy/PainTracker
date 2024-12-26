package com.example.paintracker.interfaces

import android.content.Context
import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.VisualiserLayer
import java.nio.file.Path
import java.time.LocalDate

enum class SpecialPath {
    APPDATAROOT
}

interface IPathService {
    fun initialize(context: Context)
    fun getPathAsString(path: SpecialPath): String
    fun getPath(path: SpecialPath): Path
    fun getDateDataPath(date: LocalDate): Path
    fun getPainCategoryPath(date: LocalDate, painCategory: PainCategory): Path
    fun getVisualLayerPath(date: LocalDate, layer: VisualiserLayer) : Path
}