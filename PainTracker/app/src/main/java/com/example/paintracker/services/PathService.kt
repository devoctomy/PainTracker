package com.example.paintracker.services

import android.content.Context
import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.VisualiserLayer
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.SpecialPath
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PathService : IPathService {
    private lateinit var _context: Context

    override fun initialize(context: Context) {
        _context = context
    }

    override fun getPathAsString(path: SpecialPath): String {
        return getPath(path).toString()
    }

    override fun getPath(path: SpecialPath): Path {
        return when (path) {
            SpecialPath.APPDATAROOT -> Paths.get(_context.filesDir.absolutePath, "data")
        }
    }

    override fun getPainCategoryPath(date: LocalDate, painCategory: PainCategory) : Path {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = date.format(formatter)
        val dataRoot = getPath(SpecialPath.APPDATAROOT)
        val datePath = dataRoot.resolve(datePart)
        val layerPath = datePath.resolve(painCategory.id)
        return layerPath
    }

    override fun getVisualLayerPath(date: LocalDate, layer: VisualiserLayer) : Path {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = date.format(formatter)
        val dataRoot = getPath(SpecialPath.APPDATAROOT)
        val datePath = dataRoot.resolve(datePart)
        val layerPath = datePath.resolve(layer.painCategory?.id)
        return layerPath
    }

    override fun getDateDataPath(date: LocalDate): Path {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = date.format(formatter)
        val dataRoot = getPath(SpecialPath.APPDATAROOT)
        return dataRoot.resolve(datePart)
    }
}