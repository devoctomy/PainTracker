package com.example.paintracker.services

import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IDataManagerService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.SpecialPath
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class DataManagerService @Inject constructor(
    private val pathService: IPathService
) : IDataManagerService {
    override fun ListAllPainEntries(): List<PainEntry> {
        val dataRoot = pathService.getPathAsString(SpecialPath.APPDATAROOT)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val rootDir = File(dataRoot)
        rootDir.listFiles { file -> file.isDirectory }?.forEach { dir ->
            try {
                val date = LocalDate.parse(dir.name, dateFormatter)
            } catch (e: DateTimeParseException) {
                // Handle invalid directory names gracefully
            }
        }

        return emptyList()
    }
}