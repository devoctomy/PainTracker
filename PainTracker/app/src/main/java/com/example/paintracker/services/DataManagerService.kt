package com.example.paintracker.services

import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IFileSystemService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.SpecialPath
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class DataManagerService @Inject constructor(
    private val fileSystemService: IFileSystemService,
    private val pathService: IPathService,
    private val visualiserLayerIoService: IVisualiserLayerIoService
) : IDataManagerService {
    override fun listAllPainEntries(painCategories: List<PainCategory>): List<PainEntry> {
        val painEntries: MutableList<PainEntry> = mutableListOf()
        val dataRoot = pathService.getPathAsString(SpecialPath.APPDATAROOT)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val directories = fileSystemService.listFiles(dataRoot) { dir, _ -> dir.isDirectory }

        directories?.forEach { dir ->
            try {
                val date = LocalDate.parse(dir.name, dateFormatter)
                val curPainCategories = painCategories.filter {
                    visualiserLayerIoService.painCategoryDataExists(date, it)
                }
                val notesExist = visualiserLayerIoService.notesExist(date)
                if (notesExist || curPainCategories.isNotEmpty()) {
                    painEntries.add(PainEntry(date, curPainCategories, notesExist))
                }
            } catch (e: DateTimeParseException) {
                // Handle invalid directory names gracefully
            }
        }
        return painEntries
    }
}