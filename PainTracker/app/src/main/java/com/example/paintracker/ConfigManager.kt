package com.example.paintracker

import android.content.Context
import kotlinx.serialization.json.Json

object ConfigManager {
    private var config: Config? = null

    // Initialize the ConfigManager
    fun initialize(context: Context) {
        if (config == null) {
            val json = context.assets.open("config.json").bufferedReader().use { it.readText() }
            config = Json.decodeFromString(Config.serializer(), json)
        }
    }

    // Get the Config instance
    fun getConfig(): Config {
        return config ?: throw IllegalStateException("ConfigManager is not initialized. Call initialize(context) first.")
    }
}
