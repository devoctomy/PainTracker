package com.example.paintracker

import android.content.Context
import java.nio.file.Paths

object  PathManager {
    var appDataDir: String = ""

    fun initalize(context: Context) {
        appDataDir = Paths.get(context.filesDir.absolutePath, "data").toString()
    }
}