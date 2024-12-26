package com.example.paintracker.interfaces

import android.content.Context
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
    fun getVisualLayerPath(date: LocalDate, layer: VisualiserLayer) : Path
}