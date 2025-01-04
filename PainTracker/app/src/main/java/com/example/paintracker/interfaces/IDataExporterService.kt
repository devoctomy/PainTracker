package com.example.paintracker.interfaces

import android.content.Context
import android.net.Uri

interface IDataExporterService {
    suspend fun exportDataToZip(destinationUri: Uri, context: Context): Boolean
}