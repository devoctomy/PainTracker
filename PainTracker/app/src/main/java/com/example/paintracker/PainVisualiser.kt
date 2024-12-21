package com.example.paintracker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.io.path.exists

class PainVisualiser @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var dataRoot: String = ""
    var selectedDate: LocalDate = LocalDate.now()

    var painCategories: List<PainCategory> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                visualLayers.clear()
                value.forEach { category ->
                    visualLayers.add(VisualiserLayer(category, null, null))
                }

                selectedCategory = value[0]
                updateSelectedVisualLayer()
                updateCategoryButtonColor()
                updateDrawingColor()

                loadSelectedDate()
                loadDrawing()
            }
        }

    private var imageView: ImageView
    private var frontButton: Button
    private var backButton: Button
    private var categoryButton: Button
    private var showAllButton: CheckBox
    private var isFront = true
    private var showAllLayers = false
    private var selectedCategory: PainCategory? = null
    private var selectedVisualiserLayer: VisualiserLayer? = null

    // Images for front and back
    private val frontImageRes = R.drawable.body_front
    private val backImageRes = R.drawable.body_back

    // Storage for drawing data
    private var frontDrawing: Bitmap? = null
    private var backDrawing: Bitmap? = null

    private var visualLayers: MutableList<VisualiserLayer> = mutableListOf()

    init {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.pain_visualiser, this, true)

        // Find views
        imageView = findViewById(R.id.imageView)
        frontButton = findViewById(R.id.buttonFront)
        backButton = findViewById(R.id.buttonBack)
        categoryButton = findViewById(R.id.categoryButton)
        showAllButton = findViewById(R.id.showAllButton)

        // Set initial image
        imageView.setImageResource(frontImageRes)

        frontButton.setOnClickListener {
            saveCurrentDrawing()
            isFront = true
            imageView.setImageResource(frontImageRes)
            loadDrawing()
        }

        backButton.setOnClickListener {
            saveCurrentDrawing()
            isFront = false
            imageView.setImageResource(backImageRes)
            loadDrawing()
        }

        categoryButton.setOnClickListener { showCategoryDropdown() }

        showAllButton.setOnClickListener {
            if(!showAllLayers)
            {
                saveCurrentDrawing()
            }
            showAllLayers = showAllButton.isChecked
            val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)
            signaturePad.isEnabled = !showAllLayers
            loadDrawing()
        }
    }

    private fun mergeAll(): Bitmap {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        val width = signaturePad.width
        val height = signaturePad.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)

        for (visualLayer in visualLayers) {
            val drawing = if (isFront) visualLayer.frontDrawing else visualLayer.backDrawing
            drawing?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        }

        return resultBitmap
    }

    private fun loadSelectedDate() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = selectedDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        for (visualLayer in visualLayers) {
            val layerPath = datePath.resolve(visualLayer.painCategory?.id)
            val frontPath = layerPath.resolve("front.png")
            val backPath = layerPath.resolve("back.png")

            if(frontPath.exists()) {
                visualLayer.frontDrawing = BitmapFactory.decodeFile(frontPath.toString())
                println("Image loaded from '${frontPath.toString()}'")
            }

            if(backPath.exists()) {
                visualLayer.backDrawing = BitmapFactory.decodeFile(backPath.toString())
                println("Image loaded from '${frontPath.toString()}'")
            }

            if(visualLayer == selectedVisualiserLayer) {
                frontDrawing = visualLayer.frontDrawing
                backDrawing = visualLayer.backDrawing
            }
        }
    }

    private fun saveSelectedDate() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val datePart = selectedDate.format(formatter)
        val datePath = Paths.get(dataRoot).resolve(datePart)
        val layerPath = datePath.resolve(selectedVisualiserLayer?.painCategory?.id)
        val frontPath = layerPath.resolve("front.png")
        val backPath = layerPath.resolve("back.png")

        if(isFront) {
            println("Creating directory '${frontPath.parent.toString()}'")
            Files.createDirectories(frontPath.parent)
            saveBitmapToFile(selectedVisualiserLayer?.frontDrawing!!, frontPath.toString())
        }
        else {
            println("Creating directory '${backPath.parent.toString()}'")
            Files.createDirectories(backPath.parent)
            saveBitmapToFile(selectedVisualiserLayer?.backDrawing!!, backPath.toString())
        }
    }

    private fun updateSelectedVisualLayer() {
        val curSelectedVisualiserLayer = visualLayers.find { it.painCategory == selectedCategory }
        if(curSelectedVisualiserLayer != null)
        {
            frontDrawing = curSelectedVisualiserLayer.frontDrawing
            backDrawing = curSelectedVisualiserLayer.backDrawing
            selectedVisualiserLayer = curSelectedVisualiserLayer
        }
    }

    private fun saveCurrentDrawing() {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        val bitmap = Bitmap.createBitmap(
            signaturePad.width,
            signaturePad.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        signaturePad.draw(canvas)

        if (isFront) {
            frontDrawing = bitmap
            selectedVisualiserLayer?.frontDrawing = frontDrawing
        } else {
            backDrawing = bitmap
            selectedVisualiserLayer?.backDrawing = backDrawing
        }

        saveSelectedDate()
        signaturePad.clear()
    }

    private fun saveBitmapToFile(bitmap: Bitmap, path: String) {
        val file = File(path)
        try {
            FileOutputStream(file).use { outputStream ->
                // Compress the Bitmap as a PNG (or use Bitmap.CompressFormat.JPEG for JPEG format)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            println("Image saved to '{$path}'")
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Unable to save image to $path")
        }
    }

    private fun loadDrawing() {
        val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

        var drawingToLoad = if (isFront) frontDrawing else backDrawing
        drawingToLoad = if (showAllLayers) mergeAll() else drawingToLoad

        if (drawingToLoad != null) {
            signaturePad.signatureBitmap = drawingToLoad
        } else {
            signaturePad.clear()
        }
    }

    private fun updateCategoryButtonColor() {
        selectedCategory?.let {
            categoryButton.backgroundTintList = ColorStateList.valueOf(it.colour)
        }
    }

    private fun updateDrawingColor() {
        selectedCategory?.let {
            val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)
            signaturePad.setPenColor(it.colour)
        }
    }

    private fun showCategoryDropdown() {
        val popupMenu = PopupMenu(context, categoryButton)
        painCategories.forEachIndexed { index, category ->
            val menuItem = popupMenu.menu.add(0, index, index, category.displayName)
            menuItem.isChecked = !showAllLayers && category == selectedCategory
            menuItem.isCheckable = true
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val signaturePad = findViewById<com.github.gcacace.signaturepad.views.SignaturePad>(R.id.signaturePad)

            for (i in 0 until popupMenu.menu.size()) {
                popupMenu.menu.getItem(i).isChecked = false
            }

            if(!showAllLayers)
            {
                saveCurrentDrawing()
            }
            showAllLayers = false
            showAllButton.isChecked = showAllLayers
            signaturePad.isEnabled = !showAllLayers
            menuItem.isChecked = true
            val selectedIndex = menuItem.itemId
            selectedCategory = painCategories[selectedIndex]
            updateSelectedVisualLayer()
            updateCategoryButtonColor()
            updateDrawingColor()
            loadDrawing()
            true
        }

        popupMenu.show()
    }
}
