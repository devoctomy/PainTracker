package com.example.paintracker.services

import android.content.Context
import android.content.res.AssetManager
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import io.mockk.mockk
import java.io.ByteArrayInputStream

class ConfigServiceTests {

    @Test
    fun givenContext_WhenInitialise_AndConfigExists_AndNoPainCategories_ThenIsLoaded_AndGetCurrentConfigIsNotNull_AndPainCategoriesEmpty() {
        // Arrange
        val mockContext = mockk<Context>()
        val mockAssetManager = mockk<AssetManager>()
        val configJsonContent = javaClass.classLoader?.getResource("config.json")?.readText()
            ?: "{ \"painCategories\": [] }"

        every { mockContext.assets } returns mockAssetManager
        every { mockAssetManager.open("config.json") } returns ByteArrayInputStream(configJsonContent.toByteArray())

        val configService = ConfigService()

        // Act
        configService.initialize(mockContext)

        // Assert
        assertNotNull(configService.getCurrent())
        assert(configService.getCurrent().painCategories.isEmpty())
    }

    @Test
    fun givenContext_WhenInitialise_AndConfigExists_AndPainCategories_ThenIsLoaded_AndGetCurrentConfigIsNotNull_AndPainCategoriesCorrect() {
        // Arrange
        val mockContext = mockk<Context>()
        val mockAssetManager = mockk<AssetManager>()
        val configJsonContentString = """
            {
                "painCategories": [
                    {
                      "id": "csharp",
                      "displayName": "Constant Sharp",
                      "description": "A constant sharp pain",
                      "colour": "#FFFF0000"
                    },
                    {
                      "id": "isharp",
                      "displayName": "Intermittent Sharp",
                      "description": "An intermittent sharp pain",
                      "colour": "#FFFFFF00"
                    }
                ]
            }
        """.trimIndent()
        val configJsonContent = javaClass.classLoader?.getResource("config.json")?.readText() ?: configJsonContentString

        every { mockContext.assets } returns mockAssetManager
        every { mockAssetManager.open("config.json") } returns ByteArrayInputStream(configJsonContent.toByteArray())

        val configService = ConfigService()

        // Act
        configService.initialize(mockContext)

        // Assert
        val loadedConfig = configService.getCurrent()
        assertNotNull(loadedConfig)
        assert(loadedConfig.painCategories.count() == 2)

        val firstCategory = loadedConfig.painCategories[0]
        assertEquals("csharp", firstCategory.id)
        assertEquals("Constant Sharp", firstCategory.displayName)
        assertEquals("A constant sharp pain", firstCategory.description)
        assertEquals(0xFFFF0000.toInt(), firstCategory.colour) // #FF0000 as ARGB

        val secondCategory = loadedConfig.painCategories[1]
        assertEquals("isharp", secondCategory.id)
        assertEquals("Intermittent Sharp", secondCategory.displayName)
        assertEquals("An intermittent sharp pain", secondCategory.description)
        assertEquals(0xFFFFFF00.toInt(), secondCategory.colour) // #FFFF00 as ARGB
    }
}