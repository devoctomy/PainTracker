import android.content.Context
import android.net.Uri
import com.example.paintracker.interfaces.IDataExporterService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DataExporterService : IDataExporterService {
    override suspend fun exportDataToZip(destinationUri: Uri, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val filesDir = context.filesDir
                val files = filesDir.listFiles() ?: return@withContext false

                // Load exclusion list from the text file
                val excludeList = loadExclusionList(context)

                // Use ContentResolver to write to the selected URI
                context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                    ZipOutputStream(outputStream).use { zipOut ->
                        for (file in files) {
                            val relativePath = file.absolutePath.substring(filesDir.absolutePath.length + 1)
                            if (!excludeList.contains(relativePath)) { // Check relative paths
                                addFileToZip(file, zipOut, filesDir.absolutePath, excludeList)
                            }
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun loadExclusionList(context: Context): List<String> {
        return try {
            context.assets.open("exclusion_list.txt").bufferedReader().use { it.readLines() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun addFileToZip(file: File, zipOut: ZipOutputStream, baseDirPath: String, excludeList: List<String>) {
        val relativePath = file.absolutePath.substring(baseDirPath.length + 1)
        if (excludeList.contains(relativePath)) return // Skip excluded files

        if (file.isDirectory) {
            file.listFiles()?.forEach {
                addFileToZip(it, zipOut, baseDirPath, excludeList)
            }
        } else {
            FileInputStream(file).use { fis ->
                val entry = ZipEntry(relativePath)
                zipOut.putNextEntry(entry)
                fis.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
    }
}
