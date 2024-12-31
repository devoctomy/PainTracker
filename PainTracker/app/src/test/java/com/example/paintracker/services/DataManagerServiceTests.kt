package com.example.paintracker.services

import com.example.paintracker.data.PainEntry
import com.example.paintracker.interfaces.IFileSystemService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.SpecialPath
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DataManagerServiceTests {

    private lateinit var fileSystemService: IFileSystemService
    private lateinit var pathService: IPathService
    private lateinit var visualiserLayerIoService: IVisualiserLayerIoService
    private lateinit var sut: DataManagerService

    @Before
    fun setUp() {
        fileSystemService = mockk()
        pathService = mockk()
        visualiserLayerIoService = mockk()
        sut = DataManagerService(fileSystemService, pathService, visualiserLayerIoService)
    }

    @Test
    fun givenEmptyPainCategoriesList_WhenListAllPainEntries_ThenEmptyListReturned() {
        every { pathService.getPathAsString(SpecialPath.APPDATAROOT) } returns "testPath"

        every {
            fileSystemService.listFiles(
                eq("testPath"),
                match { _ ->
                    // Always return true, or at least accept any File/filename combination
                    true
                }
            )
        } returns null

        val result = sut.listAllPainEntries(emptyList())

        assertEquals(emptyList<PainEntry>(), result)
    }

    /*@Test
    fun listAllPainEntries_returnsPainEntries_whenValidDirectoriesExist() {
        val painCategory = PainCategory(
            "Headache",
            displayName = "Display Name",
            description = "Description",
            colour = 1
        )
        val date = LocalDate.of(2023, 1, 1)
        val dir = mockk<File>()
        every { dir.name } returns "2023-01-01"
        every { dir.isDirectory } returns true
        every { pathService.getPathAsString(SpecialPath.APPDATAROOT) } returns "testPath"
        every { File("testPath").listFiles(any<FilenameFilter>()) } returns arrayOf(dir)
        every { visualiserLayerIoService.painCategoryDataExists(date, painCategory) } returns true
        every { visualiserLayerIoService.notesExist(date) } returns true

        val result = dataManagerService.listAllPainEntries(listOf(painCategory))

        assertEquals(1, result.size)
        assertEquals(date, result[0].date)
        assertEquals(listOf(painCategory), result[0].painCategories)
        assertEquals(true, result[0].hasNotes)
    }

    @Test
    fun listAllPainEntries_handlesInvalidDirectoryNamesGracefully() {
        val invalidDir = mockk<File>()
        every { invalidDir.name } returns "invalid-date"
        every { invalidDir.isDirectory } returns true
        every { pathService.getPathAsString(SpecialPath.APPDATAROOT) } returns "testPath"
        every { File("testPath").listFiles(any<FilenameFilter>()) } returns arrayOf(invalidDir)

        val result = dataManagerService.listAllPainEntries(emptyList())

        assertEquals(emptyList<PainEntry>(), result)
    }*/
}