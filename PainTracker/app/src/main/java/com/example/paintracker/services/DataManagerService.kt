package com.example.paintracker.services

import com.example.paintracker.data.PainCategory
import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.SpecialPath
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class DataManagerService @Inject constructor(
    private val pathService: IPathService,
    private val visualiserLayerIoService: IVisualiserLayerIoService
) : IDataManagerService {
    override fun listAllPainEntries(painCategories: List<PainCategory>): List<PainEntry> {
        val painEntries: MutableList<PainEntry> = mutableListOf()
        val dataRoot = pathService.getPathAsString(SpecialPath.APPDATAROOT)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val rootDir = File(dataRoot)
        rootDir.listFiles { file -> file.isDirectory }?.forEach { dir ->
            try {
                val date = LocalDate.parse(dir.name, dateFormatter)
                val curPainCategories: MutableList<PainCategory> = mutableListOf()
                painCategories.forEach { painCategory ->
                    val dataExists = visualiserLayerIoService.painCategoryDataExists(date, painCategory)
                    if(dataExists) {
                        curPainCategories.add(painCategory)
                    }
                }
                val notesExist = visualiserLayerIoService.notesExist(date)
                if(notesExist || curPainCategories.isNotEmpty()) {
                    painEntries.add(PainEntry(date, curPainCategories, notesExist))
                }

            } catch (e: DateTimeParseException) {
                // Handle invalid directory names gracefully
            }
        }

        return painEntries
    }
}