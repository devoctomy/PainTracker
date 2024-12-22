package com.example.paintracker.interfaces

import android.content.Context

enum class Path {
    APPDATAROOT
}

interface IPathService {
    fun initialize(context: Context)
    fun getPath(path: Path): String
}