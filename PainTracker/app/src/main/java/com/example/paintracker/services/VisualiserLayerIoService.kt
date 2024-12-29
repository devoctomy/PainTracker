package com.example.paintracker.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.paintracker.data.VisualiserLayer
import com.example.paintracker.interfaces.IBitmapLoaderService
import com.example.paintracker.interfaces.IPathService
import com.example.paintracker.interfaces.IVisualiserLayerIoService
import com.example.paintracker.interfaces.Side
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.io.path.exists
import android.util.Log
import com.example.paintracker.data.PainCategory

class VisualiserLayerIoService @Inject constructor(
    private val pathService: IPathService,
    private val bitmapLoaderService: IBitmapLoaderService
) : IVisualiserLayerIoService {

    override fun loadAll(localDate: LocalDate, layers: MutableList<VisualiserLayer>, width: Int, height: Int) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = localDate.format(formatter)
        Log.i("VisualiserLayerIoService", "Loading all images for '${datePart}', width = ${width}, height = ${height}.")

        for (visualLayer in layers) {
            visualLayer.frontDrawing?.recycle()
            visualLayer.backDrawing?.recycle()

            visualLayer.frontDrawing = loadLayerSideImage(localDate, visualLayer, Side.FRONT, width, height) //BitmapFactory.decodeFile(frontPath.toString()) // bitmapLoaderService.loadBitmap(frontPath.toString(), width, height)
            visualLayer.backDrawing = loadLayerSideImage(localDate, visualLayer, Side.BACK, width, height) // bitmapLoaderService.loadBitmap(backPath.toString(), width, height)
        }
    }

    override fun loadLayerSideImage(localDate: LocalDate, layer: VisualiserLayer, side: Side, width: Int, height: Int) : Bitmap? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = localDate.format(formatter)
        Log.i("VisualiserLayerIoService", "Loading layer $side image for '${datePart}', width = ${width}, height = ${height}.")

        val layerPath = pathService.getVisualLayerPath(localDate, layer)
        if(side == Side.FRONT) {
            val frontPath = layerPath.resolve("front.png")
            if(frontPath.exists()) {
                Log.i("VisualiserLayerIoService","Loading front image '${frontPath.parent}'")
                return BitmapFactory.decodeFile(frontPath.toString()) // bitmapLoaderService.loadBitmap(frontPath.toString(), width, height)
            }
        }
        else {
            val backPath = layerPath.resolve("back.png")
            if(backPath.exists()) {
                Log.i("VisualiserLayerIoService","Loading back image '${backPath.parent}'")
                return BitmapFactory.decodeFile(backPath.toString()) // bitmapLoaderService.loadBitmap(backPath.toString(), width, height)
            }
        }

        return null
    }

    override fun saveLayer(localDate: LocalDate, layer: VisualiserLayer, side: Side) {
        val layerPath = pathService.getVisualLayerPath(localDate, layer)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")
        val path = if (side == Side.FRONT) frontPath else backPath
        Log.i("VisualiserLayerIoService","Creating directory '${path.parent}'")
        Files.createDirectories(path.parent)

        if(side == Side.FRONT) {
            saveBitmapToFile(layer.frontDrawing!!, frontPath.toString())
        }
        else {
            saveBitmapToFile(layer.backDrawing!!, backPath.toString())
        }
    }

    override fun deleteLayer(localDate: LocalDate, layer: VisualiserLayer, side: Side) {
        val layerPath = pathService.getVisualLayerPath(localDate, layer)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")
        val path = if (side == Side.FRONT) frontPath else backPath
        Log.i("VisualiserLayerIoService","Creating directory '${path.parent}'")
        Files.createDirectories(path.parent)

        if(side == Side.FRONT) {
            Files.deleteIfExists(frontPath)
            layer.frontDrawing = null
        }
        else {
            Files.deleteIfExists(backPath)
            layer.backDrawing = null
        }
    }

    override fun deleteLayers(localDate: LocalDate, layers: MutableList<VisualiserLayer>) {
        for (visualLayer in layers) {
            val layerPath = pathService.getVisualLayerPath(localDate, visualLayer)
            val frontPath = layerPath.resolve("front.png")
            val backPath = layerPath.resolve("back.png")
            Files.deleteIfExists(frontPath)
            Files.deleteIfExists(backPath)
            visualLayer.frontDrawing = null
            visualLayer.backDrawing = null
        }
    }

    override fun layerDataExists(localDate: LocalDate, layer: VisualiserLayer): Boolean {
        val layerPath = pathService.getVisualLayerPath(localDate, layer)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")
        return frontPath.exists() || backPath.exists()
    }

    override fun painCategoryDataExists(localDate: LocalDate, painCategory: PainCategory): Boolean {
        val layerPath = pathService.getPainCategoryPath(localDate, painCategory)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")
        return frontPath.exists() || backPath.exists()
    }

    override fun notesExist(localDate: LocalDate): Boolean {
        val layerPath = pathService.getDateDataPath(localDate)
        val notesPath = layerPath.resolve("notes.txt")
        return notesPath.exists()
    }

    private fun saveBitmapToFile(bitmap: Bitmap, path: String) {
        val file = File(path)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            Log.i("VisualiserLayerIoService","Image saved to '{$path}'")
        } catch (e: IOException) {
            Log.e("VisualiserLayerIoService","Unable to save image to '${path}'.")
            e.printStackTrace()
            throw IOException("Unable to save image to $path")
        }
    }
}