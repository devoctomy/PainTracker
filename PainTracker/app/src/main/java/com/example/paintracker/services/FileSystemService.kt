package com.example.paintracker.services

import com.example.paintracker.interfaces.IFileSystemService
import java.io.File
import java.io.FilenameFilter

class FileSystemService : IFileSystemService {
    override fun listFiles(directoryPath: String, filter: FilenameFilter?): Array<File>? {
        return File(directoryPath).listFiles(filter)
    }
}