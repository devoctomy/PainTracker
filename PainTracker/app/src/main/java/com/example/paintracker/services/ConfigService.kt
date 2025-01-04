package com.example.paintracker.services

import android.content.Context
import com.example.paintracker.data.Config
import com.example.paintracker.interfaces.IConfigService
import kotlinx.serialization.json.Json
import java.io.File

class ConfigService : IConfigService {
    private var config: Config? = null

    override fun initialize(context: Context) {
        if (config == null) {
            val file = File(context.filesDir, "config.json")
            if (file.exists()) {
                val json = file.readText()
                config = Json.decodeFromString(Config.serializer(), json)
            } else {
                val json = context.assets.open("config.json").bufferedReader().use { it.readText() }
                config = Json.decodeFromString(Config.serializer(), json)
            }
        }
    }

    override fun getCurrent(): Config {
        return config ?: throw IllegalStateException("ConfigService is not initialized. Call initialize(context) first.")
    }

    override fun saveCurrent(context: Context) {
        val json = Json.encodeToString(Config.serializer(), getCurrent())
        val file = File(context.filesDir, "config.json")
        file.writeText(json)
    }
}