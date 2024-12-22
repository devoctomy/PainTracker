package com.example.paintracker.services

import android.content.Context
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.Path
import java.nio.file.Paths

class PathService : IPathService {
    private lateinit var _context: Context

    override fun initialize(context: Context) {
        _context = context
    }

    override fun getPath(path: Path): String {
        return when (path) {
            Path.APPDATAROOT -> Paths.get(_context.filesDir.absolutePath, "data").toString()
        }
    }
}