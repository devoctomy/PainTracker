package com.example.paintracker.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.paintracker.data.VisualiserLayer
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.Path
import com.example.paintracker.interfaces.Side
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.io.path.exists
import mu.KotlinLogging

class VisualiserLayerIoService @Inject constructor(
    private val pathService: IPathService
) : IVisualiserLayerIoService {

    private val logger = KotlinLogging.logger {}
    private val dataRoot: String by lazy { pathService.getPath(Path.APPDATAROOT) }

    override fun loadAll(localDate: LocalDate, layers: MutableList<VisualiserLayer>) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = localDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        for (visualLayer in layers) {
            val layerPath = datePath.resolve(visualLayer.painCategory?.id)
            val frontPath = layerPath.resolve("front.png")
            val backPath = layerPath.resolve("back.png")

            visualLayer.frontDrawing?.recycle()
            visualLayer.frontDrawing = null
            visualLayer.backDrawing?.recycle()
            visualLayer.backDrawing = null

            if(frontPath.exists()) {
                logger.info("Loading front image '${frontPath.parent}'")
                visualLayer.frontDrawing = BitmapFactory.decodeFile(frontPath.toString())
            }

            if(backPath.exists()) {
                logger.info("Loading back image '${backPath.parent}'")
                visualLayer.backDrawing = BitmapFactory.decodeFile(backPath.toString())
            }
        }
    }

    override fun saveLayer(localDate: LocalDate, layer: VisualiserLayer, side: Side) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = localDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val layerPath = datePath.resolve(layer.painCategory?.id)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")
        val path = if (side == Side.FRONT) frontPath else backPath
        logger.info("Creating directory '${path.parent}'")
        Files.createDirectories(path.parent)

        if(side == Side.FRONT) {
            saveBitmapToFile(layer.frontDrawing!!, frontPath.toString())
        }
        else {
            saveBitmapToFile(layer.backDrawing!!, backPath.toString())
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, path: String) {
        val file = File(path)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            logger.info("Image saved to '{$path}'")
        } catch (e: IOException) {
            logger.error("Unable to save image to '${path}'.")
            e.printStackTrace()
            throw IOException("Unable to save image to $path")
        }
    }
}