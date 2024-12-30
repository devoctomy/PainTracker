package com.example.paintracker.services

import android.content.Context
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayInputStream

class ConfigServiceTests {

    @Test
    fun givenContext_WhenInitialise_AndConfigExists_ThenIsLoaded_AnGetCurrentConfigIsNotNull() {
        // Arrange
        val mockContext = mock(Context::class.java)
        val mockAssetManager = mock(android.content.res.AssetManager::class.java)
        val configJsonContent = javaClass.classLoader?.getResource("config.json")?.readText() ?: "{ \"painCategories\": [] }"

        // Mock behavior of assets.open("config.json")
        `when`(mockContext.assets).thenReturn(mockAssetManager)
        `when`(mockAssetManager.open("config.json"))
            .thenReturn(ByteArrayInputStream(configJsonContent.toByteArray()))

        val configService = ConfigService()

        // Act
        configService.initialize(mockContext)

        // Assert
        assertNotNull(configService.getCurrent())
        assert(configService.getCurrent().painCategories.isEmpty())
    }

}