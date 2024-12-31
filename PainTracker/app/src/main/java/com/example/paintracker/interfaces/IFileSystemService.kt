package com.example.paintracker.interfaces

import java.io.File
import java.io.FilenameFilter

interface IFileSystemService {
    fun listFiles(directoryPath: String, filter: FilenameFilter?): Array<File>?
}