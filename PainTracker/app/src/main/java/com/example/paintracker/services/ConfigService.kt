package com.example.paintracker.services

import android.content.Context
import com.example.paintracker.data.Config
import com.example.paintracker.interfaces.IConfigService
import kotlinx.serialization.json.Json

class ConfigService : IConfigService {
    private var config: Config? = null

    override fun initialize(context: Context) {
        if (config == null) {
            val json = context.assets.open("config.json").bufferedReader().use { it.readText() }
            config = Json.decodeFromString(Config.serializer(), json)
        }
    }

    override fun getCurrent(): Config {
        return config ?: throw IllegalStateException("ConfigService is not initialized. Call initialize(context) first.")
    }
}